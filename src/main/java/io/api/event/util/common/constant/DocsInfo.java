package io.api.event.util.common.constant;

public class DocsInfo {

    /**
     * Link Relation Info
     */
    public static final String PROFILE = "profile";
    public static final String SELF = "self";

    public static final String INDEX = "index";

    public static final String CREATE_EVENT = "create-event";
    public static final String GET_AN_EVENT = "get-an-event";
    public static final String GET_EVENT_LIST = "get-event-list";
    public static final String UPDATE_EVENT = "update-event";



    /**
     * Dcos Path & Docs Name Info List
     */
    public static final String DOCS_PATH_PREFIX = "/docs/index.html#resources-";
    public static final String CREATE_EVENT_DOCS_PATH = DOCS_PATH_PREFIX + CREATE_EVENT;
    public static final String GET_EVENT_DOCS_PATH = DOCS_PATH_PREFIX + GET_AN_EVENT;
    public static final String GET_EVENT_LIST_DOCS_PATH = DOCS_PATH_PREFIX + GET_EVENT_LIST;
    public static final String UPDATE_EVENT_DOCS_PATH = DOCS_PATH_PREFIX + UPDATE_EVENT;

}