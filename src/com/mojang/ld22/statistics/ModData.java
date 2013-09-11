package com.mojang.ld22.statistics;

/**
 * Represents ModData for sending statistics using specfiic mod paramaters
 * @author dillyg10
 *
 */
public class ModData {
    private final long modID;
    private String name;
    private String user;
    
    public ModData(long modID, String name){
        this.modID = modID;
        this.name = name;
        //TODO Figuring out the user, and setting the parameter.
    }

    /**
     * Get the unique Mod ID for this mod. 
     * @return
     */
    protected long getModID() {
        return modID;
    }

    /**
     * Get the name of this Mod.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user currently in the mod
     * @return the user
     */
    public String getUser() {
        return user;
    }

}
