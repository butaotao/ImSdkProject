package com.dachen.mdt.entity;

import java.io.Serializable;

/**
 * Created by Mcp on 2016/8/5.
 */
public class SummaryInfo implements Serializable{
    public MdtOptionResult diagSuggest;
    public DiseaseTag tag;
    public MdtOptionResult checkSuggest;
    public MdtOptionResult treatSuggest;
    public String other;
}
