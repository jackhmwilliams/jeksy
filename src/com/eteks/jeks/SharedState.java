package com.eteks.jeks;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import com.eteks.type.UserType;

public class SharedState
{

    private Hashtable<String, TableModel> sharedModels = new Hashtable<String, TableModel>();
    private Hashtable<String, UserType> userTypes = new Hashtable<String, UserType>();
    private Hashtable<String, HashSet<JeksCell>> typeInstances = new Hashtable<String, HashSet<JeksCell>>();
    private Hashtable<TableModel, ReferringCellsListener> typeInstancesListeners = new Hashtable<TableModel, ReferringCellsListener>();

    public SharedState()
    {
    }

    public void addModel(String name, TableModel model)
    {
        sharedModels.put(name, model);
    }

    public TableModel getModel(String name)
    {
        return sharedModels.get(name);
    }

    public TableModel getModel(JeksCell cell)
    {
        return sharedModels.get(cell.getSheet());
    }

    public void addType(String name, UserType type)
    {
        userTypes.put(name, type);
    }

    public UserType getType(String name)
    {
        return userTypes.get(name);
    }

    public String getTypeConstructor(String expression)
    {
        Pattern p = Pattern.compile("=([a-zA-Z]+)(.*)$");
        Matcher matches = p.matcher(expression);
        try
        {
            matches.matches();
            if (matches.groupCount() > 1)
            {
                String type = matches.group(1);
                return userTypes.get(type) == null ? null : type;
            }
        } catch (Exception e)
        {
            return null;
        }
        return null;
    }

    public HashSet<JeksCell> getTypeInstances(String type)
    {
        return typeInstances.get(type);
    }

    public synchronized void addTypeInstanceRef(
            ReferringCellsListener listener, String type, JeksCell cell)
    {
        if (typeInstances.get(type) == null)
        {
            typeInstances.put(type, new HashSet<JeksCell>());
        }
        typeInstances.get(type).add(cell);
    }

    public synchronized void removeTypeInstanceRef(String type, JeksCell cell)
    {
        if (typeInstances.get(type) != null)
        {
            typeInstances.get(type).remove(cell);
        }
    }

    public synchronized void removeTypeInstance(JeksCell cell)
    {
        Enumeration<String> keys = typeInstances.keys();
        while (keys.hasMoreElements())
        {
            typeInstances.get(keys.nextElement()).remove(cell);
        }
    }
}