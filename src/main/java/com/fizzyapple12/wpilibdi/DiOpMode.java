package com.fizzyapple12.wpilibdi;

import java.lang.reflect.InvocationTargetException;

public abstract class DiOpMode {
    public WPILibDiContainer Container = new WPILibDiContainer();
    
    public abstract void install() throws IllegalAccessException, InstantiationException, InvocationTargetException;
}
