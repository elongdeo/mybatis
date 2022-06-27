package com.elong.deo.common.result;

import com.elong.deo.common.dal.DalPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * 前后端, 多条分页数据接口返回格式封装, 分页对象
 *
 * @author dingyinlong
 * @date 2018年04月28日10:45:04
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 7827536282487683745L;

    private static final int MAX_PAGE_SIZE = 200;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final int DEFAULT_PAGE_INDEX = 1;

    private Collection<T> items;

    private Integer totalCount;

    private Integer currentPage;

    private Integer pageSize;

    public static <T> Page<T> buildPage(Integer currentPage, Integer pageSize) {
        Page<T> page = new Page<>();
        page.setCurrentPage(currentPage);
        page.setPageSize(pageSize);
        page.correction(false);
        return page;
    }

    public static <T> Page<T> buildPage(Integer currentPage, Integer pageSize, Integer totalCount, Collection<T> items) {
        Page<T> page = buildPage(currentPage, pageSize);
        page.setTotalCount(totalCount);
        page.setItems(items);
        return page;
    }

    public static <T> Page<T> emptyPage(Integer currentPage, Integer pageSize) {
        Page<T> page = buildPage(currentPage, pageSize);
        page.setTotalCount(0);
        page.setItems(Collections.emptyList());
        return page;
    }

    public Collection<T> getItems() {
        return items;
    }

    public void setItems(Collection<T> items) {
        this.items = items;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public DalPage toDalPage() {
        if (pageSize == null || pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            pageSize = 20;
        }
        if (currentPage == null || currentPage <= 0) {
            currentPage = 1;
        }
        if (totalCount != null) {
            int maxPage = totalCount / pageSize + (totalCount % pageSize > 0 ? 1 : 0);
            if (currentPage > maxPage) {
                currentPage = maxPage;
            }
            if (currentPage == 0) {
                currentPage = 1;
            }
        }

        return new DalPage((currentPage - 1) * pageSize, pageSize);
    }

    public void correction(boolean maxPageSize) {
        Integer currentPage = getCurrentPage();
        if (currentPage == null || currentPage < DEFAULT_PAGE_INDEX) {
            setCurrentPage(DEFAULT_PAGE_INDEX);
        }
        Integer pageSize = getPageSize();
        if (pageSize == null || pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
            setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (maxPageSize && pageSize > MAX_PAGE_SIZE) {
            setPageSize(MAX_PAGE_SIZE);
        }
    }


    @Override
    public String toString() {
        return "Page{" +
                "items=" + items +
                ", totalCount=" + totalCount +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}


