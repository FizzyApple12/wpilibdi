package com.fizzyapple12.wpilibdi;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.fizzyapple12.javadi.DiInterfaces.IInitializable;
import com.fizzyapple12.javadi.DiInterfaces.IInjected;
import com.fizzyapple12.javadi.DiInterfaces.ITickable;

public abstract class DiSubsystem extends SubsystemBase implements IInjected {
    protected boolean isFinished = false;

    private boolean isTickable = false;

    private boolean needsInitialization = true;

    public void onInject() {
        if (!this.needsInitialization) return;

        this.internalInitialize();
    }

    private void internalInitialize() {
        if (this instanceof IInitializable) ((IInitializable) this).onInitialize();
        if (this instanceof ITickable) this.isTickable = true;

        needsInitialization = false;
    }

    @Override
    public void periodic() {
        //System.out.println("Subsystem periodic: " + this.getName());

        if (this.needsInitialization) return;

        //System.out.println("Subsystem periodic with init: " + this.getName());

        if (this.isTickable) ((ITickable) this).onTick();
    }
}