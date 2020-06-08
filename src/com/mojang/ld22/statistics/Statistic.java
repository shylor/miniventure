package com.mojang.ld22.statistics;

/**
 * Represents a Statistic object for querying user statistics
 * @author dillyg10
 *
 */
public class Statistic {

    /**
     * The identifier for the statistic. 
     */
    private String id;
    
    /**
     * The data contained in the statistic
     */
    private Object data;
    
    /**
     * Instintate a new statistic Object
     * @param id The statistic identifier
     * @param data The data in the statistic
     */
    public Statistic(String id, Object data){
        this.id = id;
        this.data = data;
    }

    /**
     * Returns the ID for this statistic
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @return
     */
    public Object getData() {
        return data;
    }

}
