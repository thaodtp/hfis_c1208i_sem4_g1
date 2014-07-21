/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author The
 */
@ManagedBean
@RequestScoped
public class SlotUI {
    Map<Integer,Date> slotTime;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    /**
     * Creates a new instance of SlotUI
     */
    public SlotUI() {
    }
    {
        try {
            slotTime = new HashMap<>();
            slotTime.put(1, format.parse("07:30"));
            slotTime.put(2, format.parse("09:00"));
            slotTime.put(3, format.parse("14:00"));
            slotTime.put(4, format.parse("15:30"));
            slotTime.put(5, format.parse("19:30"));
        } catch (ParseException ex) {
            Logger.getLogger(SlotUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String getDisplaySlot(int slot){
        String s = format.format(slotTime.get(slot));
        Calendar cal = Calendar.getInstance();
        cal.setTime(slotTime.get(slot));
        cal.add(Calendar.MINUTE, 90);
        s += " - "+ format.format(cal.getTime());
        return s;
    }
}
