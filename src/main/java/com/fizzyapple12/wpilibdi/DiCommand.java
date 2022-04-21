package com.fizzyapple12.wpilibdi;

import java.lang.reflect.Field;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import com.fizzyapple12.javadi.DiInterfaces.IDisposable;
import com.fizzyapple12.javadi.DiInterfaces.IInitializable;
import com.fizzyapple12.javadi.DiInterfaces.IInjected;
import com.fizzyapple12.javadi.DiInterfaces.ITickable;

public abstract class DiCommand extends CommandBase implements IInjected {
    public boolean isFinished = false;

    private boolean isInitializable = false;
    private boolean isTickable = false;
    private boolean isDisposable = false;

    private boolean needsInitialization = true;
    private boolean waitingForInject = true;
    private boolean waitingForInitialize = true;

    public void onInject() {
        this.waitingForInject = false;
        this.internalInitialize();
        /*if (!this.needsInitialization) return;

        this.waitingForInject = false;

        if (!this.waitingForInitialize) {
            this.internalInitialize();
        }*/
    }

    @Override
    public void initialize() {
        if (!this.needsInitialization) return;

        this.waitingForInitialize = false;

        if (!this.waitingForInject) {
            this.internalInitialize();
        }
    }

    private void internalInitialize() {
        if (this instanceof IInitializable) this.isInitializable = true;
        if (this instanceof ITickable) this.isTickable = true;
        if (this instanceof IDisposable) this.isDisposable = true;

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!Subsystem.class.isAssignableFrom(field.getType())) continue;

            try {
                this.addRequirements((Subsystem) field.get(this));
            } catch (Exception e) {
                this.isInitializable = false;
                this.isTickable = false;
                this.isDisposable = false;
                
                this.isFinished = true;
                
                e.printStackTrace();
            }
        }

        if (this.isInitializable) ((IInitializable) this).onInitialize();

        this.needsInitialization = false;
    }

    @Override
    public void execute() {
        if (this.needsInitialization) {
            if (!this.waitingForInject && !this.waitingForInitialize) this.internalInitialize();

            return;
        } 

        if (this.isTickable) ((ITickable) this).onTick();
    }

    @Override
    public void end(boolean interrupted) {
        this.isFinished = true;
        if (this.isDisposable) ((IDisposable) this).onDispose();
    }
  
    @Override
    public boolean isFinished() {
        return this.isFinished;
    }
}