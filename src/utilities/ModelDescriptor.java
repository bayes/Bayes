/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.io.Serializable;

public class ModelDescriptor implements Serializable{
    protected Class modelClass;
    protected String constrArg;
    protected String modelTitle;

    public ModelDescriptor(){
        super();
    }
    public ModelDescriptor(Class aclass, String atitle, String acnstArg){
        this();
        setModelClass(aclass);
        setModelTitle(atitle);
        setConstrArg(acnstArg);
    }


    public String getModelTitle () {
        return modelTitle;
    }
    public void setModelTitle ( String modelTitle ) {
        this.modelTitle = modelTitle;
    }
    public String getConstrArg () {
        return constrArg;
    }
    public void setConstrArg ( String constrArg ) {
        this.constrArg = constrArg;
    }
    public Class getModelClass () {
        return modelClass;
    }
    public void setModelClass ( Class modelClass ) {
        this.modelClass = modelClass;
    }
    
    
    @Override
    public boolean equals(Object object){
        if (object instanceof  ModelDescriptor == false) {return false;}
        ModelDescriptor md = (ModelDescriptor)object;
        
        if (md.modelClass.equals(this.modelClass)
                ||
            md.constrArg.equals(this.constrArg)
                ||
            md.modelTitle.equals(this.modelTitle)
                
         ){
            return true;
        }
        else {
            return false;
        }
        
    }
    @Override
    public int  hashCode() {
        int hash =  modelClass.hashCode();
        hash    +=  (constrArg == null)?0:constrArg.hashCode();
        hash    +=  (modelTitle == null)?0:modelTitle.hashCode();
        
        return hash;
    }
}
