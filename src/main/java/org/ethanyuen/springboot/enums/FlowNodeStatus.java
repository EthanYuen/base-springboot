package org.ethanyuen.springboot.enums;

/**
 * @author EthanYuen
 */
public interface FlowNodeStatus {
    public int getOrdinal();

    public Boolean isIgnoreSeq();

    public Boolean isSearchUnverified();
}
