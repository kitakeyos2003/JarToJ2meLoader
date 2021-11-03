package com.kitakeyos.convert;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidClassVisitor extends ClassVisitor {

    static final String TIMER_TASK = "java/util/TimerTask";
    boolean isTimerTask;

    AndroidClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, String desc, final String signature,
            final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (isTimerTask && "run".equals(name) && "()V".equals(desc)) {
            return new TimerTaskRunPatcher(mv);
        } else {
            return new AndroidMethodVisitor(mv);
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (TIMER_TASK.equals(superName)) {
            isTimerTask = true;
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
