/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.utils;

import org.springframework.context.ApplicationEvent;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

//Spring Events are by default – are synchronous.
public class PaginatedResultsRetrievedEvent<T> extends ApplicationEvent {

    private static final long serialVersionUID = 1704126143999352521L;

    private final Page<T> page;
    private final HttpHeaders headers;

    public PaginatedResultsRetrievedEvent(Object source, Page<T> pageToSet, HttpHeaders headersToSet) {
        super(source);

        page = pageToSet;
        headers = headersToSet;
    }

    @Override
    public String toString() {
        return "PaginatedResultsRetrievedEvent [page=" + page + ", headers=" + headers + "]";
    }

    public Page<T> getPage() {
        return page;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

}
