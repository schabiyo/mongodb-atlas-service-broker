package com.syolab.broker.atlasbroker.atlas;

public class AtlasException extends Exception{

    AtlasException(String message, Throwable err){
        super(message, err);
    }
}
