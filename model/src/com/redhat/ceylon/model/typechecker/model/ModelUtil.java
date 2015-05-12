package com.redhat.ceylon.model.typechecker.model;



/**
 * Model utilities, mostly used by the backends. Holds things moved from Decl.
 *
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class ModelUtil {
    public static boolean eq(Object decl, Object other) {
        if (decl == null) {
            return other == null;
        } else {
            return decl.equals(other);
        }
    }

    public static boolean equal(Declaration decl, Declaration other) {
        if (decl instanceof UnionType || decl instanceof IntersectionType
                || other instanceof UnionType || other instanceof IntersectionType) {
            return false;
        }
        return ModelUtil.eq(decl, other);
    }

    public static boolean equalModules(Module scope, Module other) {
        return eq(scope, other);
    }

    public static Package getPackage(Declaration decl){
        if (decl instanceof Scope) {
            return getPackageContainer((Scope)decl);
        } else {
            return getPackageContainer(decl.getContainer());
        }
    }

    public static Package getPackageContainer(Scope scope){
        // stop when null or when it's a Package
        while(scope != null
                && !(scope instanceof Package)){
            // stop if the container is not a Scope
            if(!(scope.getContainer() instanceof Scope))
                return null;
            scope = (Scope) scope.getContainer();
        }
        return (Package) scope;
    }

    public static Module getModule(Declaration decl){
        if (decl instanceof Scope) {
            return getModuleContainer((Scope)decl);
        } else {
            return getModuleContainer(decl.getContainer());
        }
    }

    public static Module getModuleContainer(Scope scope) {
        Package pkg = getPackageContainer(scope);
        return pkg != null ? pkg.getModule() : null;
    }

    public static ClassOrInterface getClassOrInterfaceContainer(Element decl){
        return getClassOrInterfaceContainer(decl, true);
    }
    
    public static ClassOrInterface getClassOrInterfaceContainer(Element decl, boolean includingDecl){
        if (!includingDecl) {
            decl = (Element) decl.getContainer();
        }
        // stop when null or when it's a ClassOrInterface
        while(decl != null
                && !(decl instanceof ClassOrInterface)){
            // stop if the container is not an Element
            if(!(decl.getContainer() instanceof Element))
                return null;
            decl = (Element) decl.getContainer();
        }
        return (ClassOrInterface) decl;
    }

    public static void setVisibleScope(Declaration model) {
        Scope s=model.getContainer();
        while (s!=null) {
            if (s instanceof Declaration) {
                if (model.isShared()) {
                    if (!((Declaration) s).isShared()) {
                        model.setVisibleScope(s.getContainer());
                        break;
                    }
                }
                else {
                    model.setVisibleScope(s);
                    break;
                }
            }
            else if (s instanceof Package) {
                //TODO: unshared packages!
                /*if (!((Package) s).isShared()) {
                    model.setVisibleScope(s);
                }*/
                if (!model.isShared()) {
                    model.setVisibleScope(s);
                }
                //null visible scope means visible everywhere
                break;
            }
            else {
                model.setVisibleScope(s);
                break;
            }    
            s = s.getContainer();
        }
    }

    /**
     * Determines whether the declaration's containing scope is a class or interface
     * @param decl The declaration
     * @return true if the declaration is within a class or interface
     */
    public static boolean withinClassOrInterface(Declaration decl) {
        return decl.getContainer() instanceof com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
    }

    public static boolean withinClass(Declaration decl) {
        return decl.getContainer() instanceof com.redhat.ceylon.model.typechecker.model.Class;
    }

    public static boolean isLocalToInitializer(Declaration decl) {
        return withinClass(decl) && !isCaptured(decl);
    }

    public static boolean isCaptured(Declaration decl) {
        // Shared elements are implicitely captured although the typechecker doesn't mark them that way
        return decl.isCaptured() || decl.isShared();
    }

    public static boolean isNonTransientValue(Declaration decl) {
        return (decl instanceof Value)
                && !((Value)decl).isTransient();
    }

    /**
     * Is the given scope a local scope but not an initializer scope?
     */
    public static boolean isLocalNotInitializerScope(Scope scope) {
        return scope instanceof MethodOrValue 
                || scope instanceof Constructor
                || scope instanceof ControlBlock
                || scope instanceof NamedArgumentList
                || scope instanceof Specification;
    }

    /**
     * Determines whether the declaration is local to a method,
     * getter or setter, but <strong>returns {@code false} for a declaration 
     * local to a Class initializer.</strong>
     * @param decl The declaration
     * @return true if the declaration is local
     */
    public static boolean isLocalNotInitializer(Declaration decl) {
        return isLocalNotInitializerScope(decl.getContainer());
    }
}