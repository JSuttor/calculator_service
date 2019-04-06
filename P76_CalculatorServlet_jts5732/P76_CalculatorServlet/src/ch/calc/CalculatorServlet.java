/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.calc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.beans.XMLEncoder;
/**
 *
 * @author docto
 */
public class CalculatorServlet extends HttpServlet{
    // Executed when servlet is first loaded into container.
    public void init() {
        
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String arg1 = request.getParameter("val1");
        String argOp = request.getParameter("op");
        String arg2 = request.getParameter("val2");
        String type = request.getHeader("accept");

        if (arg1 != null && (argOp == null || arg2 == null)) {
            send_typed_response(request, response, arg1);
        }
        else if (arg2 != null && (argOp == null || arg1 == null)) {
            send_typed_response(request, response, arg2);
        }
        else if (arg1 != null && arg2 != null && argOp == null) {
            send_typed_response(request, response, arg1);
        }
        else if (arg1 == null && arg2 == null){
            send_typed_response(request, response, "0");
        }
        else {
            try {
                Integer val1 = Integer.parseInt(arg1.trim());
                Integer val2 = Integer.parseInt(arg2.trim());
                Integer resp;
                if(argOp.trim().equals("1"))
                    resp = add(val1, val2);
                else if(argOp.trim().equals("2"))
                    resp = sub(val1, val2);
                else if(argOp.trim().equals("3"))
                    resp = mult(val1, val2);
                else if(argOp .trim().equals("4"))
                    resp = div (val1, val2);
                else
                    resp = 0;
                send_typed_response(request, response, resp);
            }
            catch(NumberFormatException e) {
                send_typed_response(request, response, -1);
            }
        }
    }

    private void send_typed_response(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Object data) {
        String desired_type = request.getHeader("accept");

        // If client requests plain text or HTML, send it; else XML.
        if (desired_type.contains("text/plain"))
            send_plain(response, data);
        else if (desired_type.contains("text/html"))
            send_html(response, data);
        else
            send_xml(response, data);
    }

    // For simplicity, the data are stringified and then XML encoded.
    private void send_xml(HttpServletResponse response, Object data) {
        try {
            XMLEncoder enc = new XMLEncoder(response.getOutputStream());
            enc.writeObject(data.toString());
            enc.close();
        }
        catch(IOException e) {
            throw new HTTPException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private void send_html(HttpServletResponse response, Object data) {
        String html_start =
            "<html><head><title>send_html response</title></head><body><div>";
        String html_end = "</div></body></html>";
        String html_doc = html_start + data.toString() + html_end;
        send_plain(response, html_doc);
    }

    private void send_plain(HttpServletResponse response, Object data) {
        try {
            OutputStream out = response.getOutputStream();
            out.write(data.toString().getBytes());
            out.flush();
        }
        catch(IOException e) {
            throw new HTTPException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private int add(int val1, int val2) {
        return val1+val2;
    }
    private int sub(int val1, int val2) {
        return val1-val2;
    }
    private int mult(int val1, int val2) {
        return val1*val2;
    }
    private int div(int val1, int val2) {
        if(val2 == 0)
            return 0;
        return val1/val2;
    }
}
