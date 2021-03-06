package com.technophobia.substeps.ant;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.technophobia.substeps.runner.SubstepsExecutionConfig;

public class AntExecutionConfig extends SubstepsExecutionConfig {

    private static final long serialVersionUID = 1L;

    private List<StepImplementationClassNames> stepImplementationClassNames = new ArrayList<StepImplementationClassNames>();
    private List<InitialisationClass> initialisationClasses = new ArrayList<InitialisationClass>();

    private String outputDirectory = "";

    @Override
    public List<Class<?>> getStepImplementationClasses() {
        ClassLoader loader = this.getClass().getClassLoader();

        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            StepImplementationClassNames theStepImplementationClassNames = this.stepImplementationClassNames.get(0);

            for (Param clazz : theStepImplementationClassNames.getParams()) {
                classes.add(loader.loadClass(clazz.toString()));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    @Override
    public Class<?>[] getInitialisationClasses() {
        ClassLoader loader = this.getClass().getClassLoader();

        List<Class<?>> classes = Lists.newArrayList();

        if (!this.initialisationClasses.isEmpty()) {
            try {
                InitialisationClass theInitialisationClassList = this.initialisationClasses.get(0);

                for (Param clazz : theInitialisationClassList.getParams()) {
                    classes.add(loader.loadClass(clazz.toString()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (Class<?> initClassFromSuper : super.getInitialisationClasses()) {
            classes.add(initClassFromSuper);
        }

        return classes.toArray(new Class[] {});
    }

    public void addConfiguredStepImplementationClassNames(StepImplementationClassNames s) {
        this.stepImplementationClassNames.add(s);
    }

    public void addConfiguredInitialisationClass(InitialisationClass ic) {
        this.initialisationClasses.add(ic);
    }

    //
    // Nested simple types
    //

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public static class ListOfClasses {

        private List<Param> params = new ArrayList<Param>();

        public void addConfiguredParam(Param param) {
            this.params.add(param);
        }

        public List<Param> getParams() {
            return this.params;
        }
    }

    public static class StepImplementationClassNames extends ListOfClasses {
        //
    }

    public static class InitialisationClass extends ListOfClasses {
        //
    }

    public static class Param {

        private String value;

        public String getText() {
            return value;
        }

        public void addText(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

}
