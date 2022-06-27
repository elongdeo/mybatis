/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.config;

/**
 * 生成Repo配置
 * 
 * @author dingyinlong
 */
public class GeneratedRepo {
    private String repoSuffix;
    private String repoPackage;
    private String repoImplPackage;
    private Boolean repoNoInterface;
    private String autowiredSequenceName;

    public GeneratedRepo(String repoSuffix, String repoPackage, String repoImplPackage, Boolean repoNoInterface, String autowiredSequenceName) {
        this.repoSuffix = repoSuffix;
        this.repoPackage = repoPackage;
        this.repoImplPackage = repoImplPackage;
        this.repoNoInterface = repoNoInterface;
        this.autowiredSequenceName = autowiredSequenceName;
    }

    public String getRepoSuffix() {
        return repoSuffix;
    }

    public void setRepoSuffix(String repoSuffix) {
        this.repoSuffix = repoSuffix;
    }

    public String getRepoPackage() {
        return repoPackage;
    }

    public void setRepoPackage(String repoPackage) {
        this.repoPackage = repoPackage;
    }

    public String getRepoImplPackage() {
        return repoImplPackage;
    }

    public void setRepoImplPackage(String repoImplPackage) {
        this.repoImplPackage = repoImplPackage;
    }

    public Boolean getRepoNoInterface() {
        return repoNoInterface;
    }

    public void setRepoNoInterface(Boolean repoNoInterface) {
        this.repoNoInterface = repoNoInterface;
    }

    public String getAutowiredSequenceName() {
        return autowiredSequenceName;
    }

    public void setAutowiredSequenceName(String autowiredSequenceName) {
        this.autowiredSequenceName = autowiredSequenceName;
    }
}
