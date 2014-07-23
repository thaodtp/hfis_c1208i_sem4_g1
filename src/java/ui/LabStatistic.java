/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import entity.Lab;
import entity.LabSchedule;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author The
 */
public class LabStatistic {

    List<Lab> source;
    Map<String, List<Integer>> temp;
    List<String> keys;
    int labs;
    public LabStatistic(List<Lab> source) {
        this.source = source;
        labs = source.size();
        keys = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("\"dd/MM/yyyy\"");
        for (Lab l : source) {
            for (LabSchedule req : l.getLabScheduleList()) {
                String date = sdf.format(req.getDate());
                if (!keys.contains(date)) {
                    keys.add(date);
                }
            }
        }
        Collections.sort(keys);
        temp = new TreeMap<>();
        for (Lab l : source) {
            List<Integer> values = new LinkedList<>();
            for (String key : keys) {
                int count = 0;
                for (LabSchedule req : l.getLabScheduleList()) {
                    if (key.equals("\"" + req.getDisplayDate() + "\"")) {
                        count += 1;
                    }
                }
                values.add(count);
            }
            temp.put(l.getName(), values);
        }
    }

    public String getValues() {
        String s = "";
        for (String name : temp.keySet()) {
            s += "{data: " + temp.get(name) + ",name: '" + name + "'},";
        }
        if (s.endsWith(",")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public String getPerformance() {
        List<Integer> used = new LinkedList<>();
        int index = 0;
        for (String key : keys) {
            used.add(0);
            for (String name : temp.keySet()) {
                List<Integer> t = temp.get(name);
                used.set(index, used.get(index)+t.get(index));
            }
            index++;
        }
        String value1="";
        String value2="";
        int total = labs*5;
        for(Integer count:used){
            value1=value1+count+",";
            value2=value2+(total-count)+",";
        }
        if(value1.endsWith(",")) value1=value1.substring(0, value1.length()-1);
        if(value2.endsWith(",")) value2=value2.substring(0, value2.length()-1);
        return "{data:["+value2+"],name: 'Unused'},{data: ["+value1+"],name: 'Used'}";
    }

    private int getTotal(List<Integer> source) {
        int result = 0;
        for (Integer s : source) {
            result += s;
        }
        return result;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

}
