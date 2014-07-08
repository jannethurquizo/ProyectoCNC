/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opencnc.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencnc.beans.Arco;
import com.opencnc.beans.ElementoGrafico;
import com.opencnc.beans.GetJson;
import com.opencnc.beans.Linea;
import com.opencnc.beans.Modelo;
import com.opencnc.beans.Texto;
import com.opencnc.beans.lineatool;
import com.opencnc.util.HibernateUtil;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author root
 */
@Controller

public class ElementoGraficoController {
    int ident = 0;
    // Implemento Log4j para eventos tipo log
    private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());
    
    @RequestMapping(    value="elemento/crear/linea/lista", 
                        method=RequestMethod.GET,
                        headers = "Accept=*/*"
                        )
    public void listaLinea(@RequestParam(value = "datos", required = true) String datos, 
                            
                            //@ModelAttribute ElementoGrafico elemento,
                            HttpServletRequest request, 
                            HttpServletResponse response) throws Exception{
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria cs = s.createCriteria(ElementoGrafico.class);
        List<ElementoGrafico> leg = cs.list();
        
        int index=0;
        
        index = leg.size() + 1;
        
        Gson gson = new Gson();
       
        Type collectionType = new TypeToken<List<lineatool>>(){}.getType();
        List<lineatool> ints2 = gson.fromJson(datos, collectionType);
      
        Iterator<lineatool> elem = ints2.iterator();
        
        
        
        while(elem.hasNext()){
            
           ElementoGrafico elemento = new ElementoGrafico();
            
            lineatool tipo = elem.next();
              
            Modelo u = (Modelo)s.get(Modelo.class,ident);
           
            elemento.setModelo(u);
            Calendar c = new GregorianCalendar();
            Date d1 = c.getTime();
            elemento.setCreadoFecha(d1);
            elemento.setPosicionX(tipo.getX1());
            elemento.setPosicionY(tipo.getY1());
            elemento.setElementoId(index);
            elemento.setTipoElemento(tipo.getType());
            elemento.setCreadoPor(u.getCreadoPor());
            //falta seguir 
            Transaction t = s.getTransaction();
            s.beginTransaction();
            s.saveOrUpdate(elemento);
            t.commit();
            
            
            index++;
           
            switch (tipo.getType()) {
                case 1:  System.out.print("es punto");
                         break;
                case 2:  System.out.print("es linea");
                        
                         Session ss = HibernateUtil.getSessionFactory().openSession();
                         Linea l = new Linea();
                         ElementoGrafico gr = (ElementoGrafico)ss.get(ElementoGrafico.class, elemento.getElementoId());
                         l.setElementoGrafico(gr);
                         l.setPosicionX2(tipo.getX2());
                         l.setPosicionY2(tipo.getY2());
                         
                         Transaction tt = ss.getTransaction();
                         ss.beginTransaction();
                         ss.saveOrUpdate(l);
                         tt.commit();
                         
                         break;
                case 3:  System.out.print("es circulo");
                         break;
                case 4:  System.out.print("es rectangulo");
                         break;
                case 5:  System.out.print("es arco");
                         Session s1 = HibernateUtil.getSessionFactory().openSession();
                         Arco arc = new Arco();
                         
                         ElementoGrafico gr1 = (ElementoGrafico)s1.get(ElementoGrafico.class, elemento.getElementoId());
                         arc.setElementoGrafico(gr1);
                         arc.setRadio(tipo.getRadius());
                         arc.setAngulo1(tipo.getX2());
                         arc.setAngulo2(tipo.getY2());
                         
                         Transaction t1 = s1.getTransaction();
                         s1.beginTransaction();
                         s1.saveOrUpdate(arc);
                         
                         t1.commit();
                         
                         break;
                case 6:  System.out.print("es label");
                         break;
                case 7:  System.out.print("es texto");
                         Texto tx = new Texto();
                         tx.setTamanio(12);
                         
                         break;
                case 8:  System.out.print("es circulo");
                         break;
                case 9:  System.out.print("es circulo");
                         break;
                case 10:  System.out.print("es circulo");
                         break;
                default: System.out.print("no es");
                         break;
            }
        }
    }
    
    @RequestMapping(    value="elemento/crear/linea/getJSON", 
                        method=RequestMethod.GET,headers = "Accept=*/*")
    public @ResponseBody String getJSON(){
        
        
        String ss = "holas";
                
        Session s = HibernateUtil.getSessionFactory().openSession();
            
        Modelo mod = (Modelo)s.get(Modelo.class, ident);

        Criteria c = s.createCriteria(ElementoGrafico.class);
        c.add(Restrictions.eq("modelo", mod));
        List<ElementoGrafico> leg = c.list();
        
        return ss;
    }
    
   
    @RequestMapping  ("/elemento/lista")
    public ModelAndView   lista  (HttpServletRequest request, 
                                            HttpServletResponse response)
                                            throws Exception{
        Session  s = HibernateUtil.getSessionFactory().openSession();
        
        Criteria  c =s.createCriteria(ElementoGrafico.class);
        
        List<ElementoGrafico> l = c.list();
        
        ModelAndView m = new ModelAndView("/elemento/lista");
        m.addObject("sentencia",l);
 
        return m;
    }
    
    @RequestMapping  ("/elemento/crear/{id}")
    public ModelAndView   crear  (@PathVariable Integer id,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) throws Exception{
        HttpSession sess =  request.getSession();
        if (sess != null){
            Session  s = HibernateUtil.getSessionFactory().openSession();
        
            //ElementoGrafico e = new ElementoGrafico();
            ModelAndView m = new ModelAndView("/elemento/crear");
            m.addObject("Id",id);
            Modelo mod = (Modelo)s.get(Modelo.class, id);
            m.addObject("NombreModel",mod.getNombre());
            //m.addObject("TipoMaquina",mod.getTipoMaquina());
            ident = id;
            
            
            /*
            Session s = HibernateUtil.getSessionFactory().openSession();
            Modelo u = (Modelo)s.get(Modelo.class,id);
            e.setModelo(u);
            //falta seguir 
            Transaction t = s.getTransaction();
            s.beginTransaction();
            s.saveOrUpdate(e);
            t.commit();
            
            */
           //m.addObject("elementoGrafico",e);
            return m;
        }else{
             request.removeAttribute("usuario");
            return new ModelAndView("redirect:/usuario/login.htm");
        }
    }
    
    @RequestMapping  ("/elemento/actualizar")
    public ElementoGrafico   actualizar  (lineatool ln,
                                        
                                        @RequestParam Integer Id,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) throws Exception{
        //HttpSession sess =  request.getSession();
        //if (sess != null){
            ElementoGrafico e = new ElementoGrafico();
            Session s = HibernateUtil.getSessionFactory().openSession();
            
            Modelo u = (Modelo)s.get(Modelo.class,Id);
            e.setModelo(u);
            Calendar c = new GregorianCalendar();
            Date d1 = c.getTime();
            e.setCreadoFecha(d1);
            e.setPosicionX(ln.getX1());
            e.setPosicionY(ln.getY1());
            //falta seguir 
            Transaction t = s.getTransaction();
            s.beginTransaction();
            s.saveOrUpdate(e);
            t.commit();
            
            return e; 
       // }else{
        //     request.removeAttribute("usuario");
         //   return new ModelAndView("redirect:/usuario/login.htm");
        //}
    }
    @RequestMapping  ("/elemento/borrar/{id}")
    public ModelAndView   borrar  (@PathVariable Integer id,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) throws Exception{
        HttpSession sess =  request.getSession();
        if (sess != null){
           Session s = HibernateUtil.getSessionFactory().openSession();
            ElementoGrafico e = (ElementoGrafico) s.get(ElementoGrafico.class, id);
            Transaction t = s.beginTransaction();
            s.delete(e);
            t.commit();

            return lista(request , response);
        }else{
             request.removeAttribute("usuario");
            return new ModelAndView("redirect:/usuario/login.htm");
        }
        
    }
    
    @RequestMapping  ("/elemento/obtenerElemento")
    public ModelAndView   obtenerElemento  (@PathVariable Integer id,
                                                HttpServletRequest request, 
                                                HttpServletResponse response) throws Exception{
        HttpSession sess =  request.getSession();
        if (sess != null){
           return null; 
        }else{
             request.removeAttribute("usuario");
            return new ModelAndView("redirect:/usuario/login.htm");
        }
    }
    
    @RequestMapping  ("/elemento/obtenerElementoPorModelo")
    public ModelAndView   obtenerElementoPorModelo  (int ModeloID,
                                                        HttpServletRequest request, 
                                                        HttpServletResponse response) throws Exception{
        HttpSession sess =  request.getSession();
        if (sess != null){
           return null; 
        }else{
             request.removeAttribute("usuario");
            return new ModelAndView("redirect:/usuario/login.htm");
        }
    }  

    /**
     * @return the modelID
     */
   
}
