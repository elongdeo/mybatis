package org.mybatis.generator.internal;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.MergeConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.api.dom.OutputUtilities.newLine;

public class JavaFileMergerJaxp {
    public static String getNewJavaFile(GeneratedJavaFile gjf, String existingFileFullPath)
        throws FileNotFoundException {
        CompilationUnit newCompilationUnit = JavaParser.parse(gjf.getFormattedContent());
        CompilationUnit existingCompilationUnit = JavaParser.parse(new File(existingFileFullPath));
        if (gjf.getCompilationUnit() instanceof Interface) {
            return mergerInteface(gjf, newCompilationUnit, existingCompilationUnit);
        } else {
            // 只支持接口合并
            throw new UnsupportedOperationException();
        }
    }

    public static String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit existingCompilationUnit) {

        StringBuilder sb = new StringBuilder(newCompilationUnit.getPackageDeclaration().get().toString());
        newCompilationUnit.removePackageDeclaration();

        //合并imports
        NodeList<ImportDeclaration> imports = newCompilationUnit.getImports();
        imports.addAll(existingCompilationUnit.getImports());
        Set importSet = new HashSet<ImportDeclaration>();
        importSet.addAll(imports);

        NodeList<ImportDeclaration> newImports = new NodeList<>();
        newImports.addAll(importSet);
        newCompilationUnit.setImports(newImports);
        for (ImportDeclaration i : newCompilationUnit.getImports()) {
            sb.append(i.toString());
        }
        newLine(sb);
        NodeList<TypeDeclaration<?>> types = newCompilationUnit.getTypes();
        NodeList<TypeDeclaration<?>> oldTypes = existingCompilationUnit.getTypes();

        for (int i = 0; i < types.size(); i++) {
            //截取Class
            String classNameInfo = types.get(i).toString().substring(0, types.get(i).toString().indexOf("{") + 1);
            sb.append(classNameInfo);
            newLine(sb);
            newLine(sb);
            //合并fields
            List<FieldDeclaration> fields = types.get(i).getFields();
            List<FieldDeclaration> oldFields = oldTypes.get(i).getFields();
            List<FieldDeclaration> newFields = new ArrayList<>();
            HashSet<FieldDeclaration> fieldDeclarations = new HashSet<>();
            fieldDeclarations.addAll(fields);
            fieldDeclarations.addAll(oldFields);
            newFields.addAll(fieldDeclarations);
            for (FieldDeclaration f : newFields) {
                sb.append(f.toString());
                newLine(sb);
                newLine(sb);
            }

            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();
            List<MethodDeclaration> existingMethods = oldTypes.get(i).getMethods();
            for (MethodDeclaration f : methods) {
                sb.append(f.toString());
                newLine(sb);
                newLine(sb);
            }
            for (MethodDeclaration m : existingMethods) {
                boolean flag = true;
                for (String tag : MergeConstants.OLD_ELEMENT_TAGS) {
                    if (m.toString().contains(tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    sb.append(m.toString());
                    newLine(sb);
                    newLine(sb);
                }
            }

            //判断是否有内部类
            types.get(i).getChildNodes();
            for (Node n : types.get(i).getChildNodes()) {
                if (n.toString().contains("static class")) {
                    sb.append(n.toString());
                }
            }

        }

        return sb.append(System.getProperty("line.separator") + "}").toString();
    }

    public static String mergerInteface(GeneratedJavaFile gjf, CompilationUnit newCompilationUnit,
                                        CompilationUnit existingCompilationUnit) {
        Interface interfaze = (Interface)gjf.getCompilationUnit();
        // 加入原有文件的import
        NodeList<ImportDeclaration> existingImports = existingCompilationUnit.getImports();
        existingImports.forEach(importDeclaration -> {
            if (importDeclaration.isStatic()) {
                String nameAsString = importDeclaration.getNameAsString();
                if (importDeclaration.getTokenRange().get().toString().endsWith(".*;")) {
                    nameAsString = nameAsString + ".*";
                }
                interfaze.getStaticImports().add(nameAsString);
            } else {
                interfaze.getImportedTypes().add(
                    new FullyQualifiedJavaType(importDeclaration.getNameAsString()));
            }
        });

        NodeList<TypeDeclaration<?>> types = existingCompilationUnit.getTypes();

        for (int i = 0; i < types.size(); i++) {
            //合并methods
            List<MethodDeclaration> methods = types.get(i).getMethods();
            methods.stream().filter(methodDeclaration -> !methodExists(interfaze, methodDeclaration)).forEach(
                methodDeclaration -> addMethod(interfaze, methodDeclaration));
        }

        return gjf.getFormattedContent();
    }

    private static boolean methodExists(Interface interfaze, MethodDeclaration methodDeclaration) {
        return interfaze.getMethods().stream().anyMatch(method -> {
            if (!method.getName().equals(methodDeclaration.getName().getIdentifier())) {
                return false;
            }
            List<Parameter> parameters = method.getParameters();
            NodeList<com.github.javaparser.ast.body.Parameter> parameterNodeList = methodDeclaration.getParameters();
            if (parameters.size() != parameterNodeList.size()) {
                return false;
            }
            for (int i = 0; i < parameters.size(); i++) {
                if (!parameters.get(i).getType().getFullyQualifiedName().equals(parameterNodeList.get(i).getType()
                    .asString()) && !parameters.get(i).getType().getShortName().equals(parameterNodeList.get(i)
                    .getType().asString())) {
                    return false;
                }
            }
            return true;
        });
    }

    private static void addMethod(Interface interfaze, MethodDeclaration methodDeclaration) {
        Method method = new Method();
        method.setName(methodDeclaration.getName().getIdentifier());
        method.setReturnType(new FullyQualifiedJavaType(methodDeclaration.getType().asString()));
        Comment comment = methodDeclaration.getComment().orElse(null);
        if (comment != null) {
            String commentString = comment.toString();
            method.addJavaDocLine(commentString.substring(0, commentString.length() - 1));
        }
        methodDeclaration.getParameters().forEach(parameter -> {
            Parameter parameter1 = new Parameter(new FullyQualifiedJavaType(parameter.getType().asString()), parameter
                .getNameAsString(), parameter.getTokenRange().get().toString().contains("..."));
            parameter.getAnnotations().forEach(
                annotationExpr -> parameter1.addAnnotation(annotationExpr.getTokenRange().get().toString()));
            method.addParameter(parameter1);
        });
        methodDeclaration.getAnnotations().forEach(
            annotationExpr -> method.addAnnotation(annotationExpr.getTokenRange().get().toString())
        );
        interfaze.getMethods().add(method);
    }
}