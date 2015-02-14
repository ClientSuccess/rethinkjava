package com.dkhenry.RethinkDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.*;
import com.dkhenry.RethinkDB.errors.RqlDriverException;

public class Datum {
	/* Datum Constructors */

    public static Object datum() {
        return JSONObject.NULL;
    }
	/* We will specialize for all types defined in the protocol */
    public static Object datum(Boolean b) {    	
    	return b;
    }
    public static Object datum(String s) {
        return s;
    }
    public static Object datum(Double d) {
    	return d;
    }
    
    /* We want to cast all "Numbers" to Doubles */
    public static<T extends Number> Object datum(T n) {
    	return datum(n.doubleValue());
    }

    /* For any type we haven't specialized we are going to cast to a string */ 
    public static <T> Object datum(T t) {
        if( null == t ) {
            return datum();
        } else if(t instanceof Boolean) {
    		return datum((Boolean) t);
    	} else if( Number.class.isAssignableFrom(t.getClass()) ) {
    		return datum((Number) t);
    	} else if( t instanceof List) { 
    		return datum((List) t);
    	} else if( t instanceof Map) {
    		return datum((Map) t);
    	} else {
    		return datum(t.toString());
    	}
    }
    
    // The R Array
    public static <T> Object datum(List<T> a) {
    	JSONArray b = new JSONArray();
    	for(T value: a) {
    		b.put(datum(value));
    	}
    	return b;
    }
    
    // This is the R_OBJECT
    public static <K,V> Object datum(Map<K,V> h){
    	JSONObject b = new JSONObject();
    	for(Entry<K, V> entry: h.entrySet()) {
    		b.put(entry.getKey().toString(), datum(entry.getValue()));
    	}
    	return b;
    }

    public static Object deconstruct(Boolean d) throws RqlDriverException {
        return d;
    }

    public static Object deconstruct(Double d) throws RqlDriverException {
        return d;
    }

    public static Object deconstruct(String d) throws RqlDriverException {
        return d;
    }

    public static Object deconstruct(JSONArray d) throws RqlDriverException {
        ArrayList<Object> l = new ArrayList<Object>(); 
		for(int i = 0; i < d.length(); ++i) {
			l.add(deconstruct(d.get(i)));
		}
		return l;
    }

    public static Object deconstruct(JSONObject d) throws RqlDriverException {
        HashMap<String,Object> m = new HashMap<String, Object>();
        Iterator<String> keys = d.keys();
        while(keys.hasNext()) {
            String key = keys.next();
			m.put(key, deconstruct(d.get(key)));
		}
		return m;
    }

    public static Object deconstruct(Object d) throws RqlDriverException {
        if (d == null || d == JSONObject.NULL) {
            return null;
        } else if(d instanceof JSONObject) {
            return deconstruct((JSONObject)d);
        } else if(d instanceof JSONArray) {
            return deconstruct((JSONArray)d);
        } else if(d instanceof Boolean) {
            return deconstruct((Boolean)d);
        } else if(d instanceof Number) {
            return deconstruct(new Double(((Number)d).doubleValue()));
        } else if(d instanceof String) {
            return deconstruct((String)d);
        } else {
            throw new RqlDriverException("Unknown Datum Type " + d.getClass().getName() + " presented for Deconstruction") ;
        }
    }
}
