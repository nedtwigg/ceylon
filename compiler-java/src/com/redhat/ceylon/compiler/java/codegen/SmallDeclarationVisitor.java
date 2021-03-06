package com.redhat.ceylon.compiler.java.codegen;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Value;

/**
 * Sets the underlying type of declarations that have been marked as 
 * {@code small} during type analysis.
 */
public class SmallDeclarationVisitor extends Visitor {
    
    /**
     * Return a type that's the same as the given type, but with the 
     * underlying type set.
     */
    static Type smallUnderlyingType(Type type) {
        Type result;
        if (type.isInteger()) {
            result = type.withoutUnderlyingType();
            result.setUnderlyingType("int");
            return result;
        } else if (type.isFloat()) {
            result = type.withoutUnderlyingType();
            result.setUnderlyingType("float");
        } else {
            result = type;
        }
        return result;
    }
    
    /** 
     * If the declaration is {@code small} set the declaration's type's 
     * underlying type to {@code int} or {@code float}.
     */
    static void smallDeclaration(Declaration d) {
        if (Decl.isSmall(d)) {
            FunctionOrValue f = (FunctionOrValue)d;
            setSmallType(f);
        }
    }

    protected static void setSmallType(FunctionOrValue f) {
        Type t = smallUnderlyingType(f.getType());
        f.setType(t);
        if (f instanceof Value) {
            Setter s = ((Value)f).getSetter();
            if (s != null) {
                s.getParameter().getModel().setType(t);
            }
        }
    }
    
    @Override
    public void visit(Tree.Parameter that) {
        super.visit(that);
        smallDeclaration(that.getParameterModel().getModel());
    }
    
    @Override
    public void visit(Tree.ParameterDeclaration that) {
        super.visit(that);
        smallDeclaration(that.getTypedDeclaration().getDeclarationModel());
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Value getter = that.getDeclarationModel().getGetter();
        if (getter!=null && getter.isSmall()) {
            // Setter parameter is small if getter is small
            smallDeclaration(that.getDeclarationModel().getParameter().getModel());
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.AnyMethod that) {
        //Declaration prevd = declaration;
        //declaration = that.getDeclarationModel();
        //if (that.getDeclarationModel().getRefinedDeclaration())
        if (Decl.isSmall(that.getDeclarationModel())) {
            smallDeclaration(that.getDeclarationModel());
        }
        super.visit(that);
        //declaration = prevd;
    }

    
    @Override
    public void visit(Tree.AnyAttribute that) {
        //Declaration prevd = declaration;
        //declaration = that.getDeclarationModel();
        if (Decl.isSmall(that.getDeclarationModel())) {
            smallDeclaration(that.getDeclarationModel());
        }
        super.visit(that);
        //declaration = prevd;
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Declaration d = that.getDeclaration();
        if (d == null) {
            if (that.getBaseMemberExpression() instanceof Tree.MemberOrTypeExpression) {
                d = ((Tree.MemberOrTypeExpression)that.getBaseMemberExpression()).getDeclaration();
            }
        }
        
        //Declaration prevd = declaration;
        //declaration = d;
        smallDeclaration(d);
        super.visit(that);
        //declaration = prevd;
    }
    

    @Override
    public void visit(Tree.AttributeArgument that) {
        if (Decl.isSmall(that.getParameter().getModel())) {
            that.getDeclarationModel().setSmall(true);
            smallDeclaration(that.getDeclarationModel());
        }
        super.visit(that);
    }
}
