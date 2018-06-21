package com.aseanmobile.wikipediamobile.support ;

/**
 *
 */
public interface HeavyWorkerDelegate {
    
    /**
     * Do assigned work as background process
     * 
     * @return result of the work done
     */
    public Object performInBackground ( ) ;
    
    /**
     * Do call when performInBackground work is done
     * 
     * @param result
     *            result of performInBackground method
     */
    public void callback ( Object result ) ;
    
    /**
     * This method will called after callback for displaying a result or a
     * error.
     */
    public void finalResult ( ) ;
}
