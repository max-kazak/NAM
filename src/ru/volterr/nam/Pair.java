package ru.volterr.nam;

import java.io.Serializable;

public class Pair<F, S> implements Serializable{
    private F first; //first member of pair
    private S second; //second member of pair

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
    
    @Override
    public boolean equals(Object o) { 
      
      if (this.getClass() == o.getClass()) { 
        Pair<?, ?> p1 = (Pair<?, ?>) o;
        
        if(  (this.first != null)&&( !this.first.equals(p1.getFirst()) )  )
        	return false;
        if(  (this.second != null)&&( !this.second.equals(p1.getSecond()) )  )
        	return false;
        if(  (this.first == null)&&(p1.getFirst() != null) )
        	return false;
        if(  (this.second == null)&&(p1.getSecond() != null) )
        	return false;
        //otherwise
        return true;
        
      }
      return(false);
    }
    
    @Override
    public int hashCode() {
          int hash = 37;
          hash = hash*17 + first.hashCode();
          hash = hash*17 + second.hashCode();
                       
          return hash;
    }
    
}