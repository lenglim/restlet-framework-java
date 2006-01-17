/*
 * Copyright 2005-2006 J�r�me LOUVEL
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * http://www.opensource.org/licenses/cddl1.txt
 * If applicable, add the following below this CDDL
 * HEADER, with the fields enclosed by brackets "[]"
 * replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package com.noelios.restlet.ext.jetty;

import org.mortbay.util.InetAddrPort;
import org.restlet.UniformCall;
import org.restlet.UniformInterface;
import org.restlet.connector.HttpServer;
import org.restlet.connector.HttpServerCall;

/**
 * Jetty HTTP server connector.
 * @see <a href="http://jetty.mortbay.com/">Jetty home page</a>
 */
public class JettyServer implements HttpServer
{
   /** Serial version identifier. */
   private static final long serialVersionUID = 1L;

   /** AJP 1.3 protocol listener. */
   public static final int LISTENER_AJP = 1;
   
   /** HTTP listener. */
   public static final int LISTENER_HTTP = 2;
   
   /** HTTPS (SSL) listener. */
   public static final int LISTENER_HTTPS = 3;

   /** The name of this REST connector. */
   protected String name;

   /** The target handler. */
   protected UniformInterface target;

   /** The Jetty listener type. */
   protected int listenerType;
   
   /** The Jetty listener. */
   protected org.mortbay.http.HttpListener listener;
   
   /** The Jetty listening port if specified. */
   protected int port;
   
   /** The Jetty listening address if specified. */
   protected InetAddrPort address;
   
   protected String keystorePath;
   
   protected String keystorePassword;
   
   protected String keyPassword;
   
   /**
    * Constructor.
    * @param name The unique connector name.
    * @param target The target handler.
    * @param listenerType The listener type.
    * @param port The Jetty listening port.
    */
   public JettyServer(String name, UniformInterface target, int listenerType, int port)
   {
      this.name = name;
      this.target = target;
      this.listenerType = listenerType;
      this.port = port;
      this.address = null;
   }

   /**
    * Constructor.
    * @param name The unique connector name.
    * @param target The target component handling calls.
    * @param listenerType The listener type.
    * @param address The Jetty listening address.
    */
   public JettyServer(String name, UniformInterface target, int listenerType, InetAddrPort address)
   {
      this.name = name;
      this.address = address;
      this.target = target;
      this.listenerType = listenerType;
      this.address = address;
      this.port = -1;
   }

   /**
    * Configure the SSL listener.
    * @param keystorePath
    * @param keystorePassword
    * @param keyPassword
    */
   public void configureSSL(String keystorePath, String keystorePassword, String keyPassword)
   {
      this.keystorePath = keystorePath;
      this.keystorePassword = keystorePassword;
      this.keyPassword = keyPassword;
   }
   
   /**
    * Returns the Jetty listener.
    * @return The Jetty listener.
    */
   public org.mortbay.http.HttpListener getListener()
   {
      return this.listener;
   }
   
   /** Start hook. */
   public void start()
   {
      try
      {
         switch(this.listenerType)
         {
            case LISTENER_AJP:
               if(this.address != null)
               {
                  this.listener = new AjpListener(this, this.address);
               }
               else
               {
                  this.listener = new AjpListener(this);
                  this.listener.setPort(port);
               }
            break;
            
            case LISTENER_HTTP:
               if(this.address != null)
               {
                  this.listener = new HttpListener(this, this.address);
               }
               else
               {
                  this.listener = new HttpListener(this);
                  this.listener.setPort(port);
               }
            break;
            
            case LISTENER_HTTPS:
               if(this.address != null)
               {
                  HttpsListener httpsListener = new HttpsListener(this, this.address);
                  httpsListener.setKeystore(this.keystorePath);
                  httpsListener.setPassword(this.keystorePassword);
                  httpsListener.setKeyPassword(this.keyPassword);
                  this.listener = httpsListener;
               }
               else
               {
                  this.listener = new HttpsListener(this);
                  this.listener.setPort(port);
               }
            break;
         }
         
         getListener().start();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }

   /** Stop hook. */
   public void stop()
   {
      try
      {
         getListener().stop();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Indicates if the connector is started.
    * @return True if the connector is started.
    */
   public boolean isStarted()
   {
      return getListener().isStarted();
   }

   /**
    * Indicates if the connector is stopped.
    * @return True if the connector is stopped.
    */
   public boolean isStopped()
   {
      return !isStarted();
   }

   /**
    * Returns the target handler.
    * @return The target handler.
    */
   public UniformInterface getTarget()
   {
      return target;
   }

   /**
    * Sets the target handler.
    * @param target The target handler.
    */
   public void setTarget(UniformInterface target)
   {
      this.target = target;
   }

   /**
    * Handles the HTTP protocol call.<br/>
    * The default behavior is to create an UniformCall and invoke the "handle(UniformCall)" method.
    * @param call The HTTP protocol call.
    */
   public void handle(HttpServerCall call)
   {
      UniformCall uniformCall = call.toUniform();
      handle(uniformCall);
      call.fromUniform(uniformCall);
   }

   /**
    * Handles a uniform call.
    * The default behavior is to as the attached handler to handle the call.
    * @param call The uniform call to handle.
    */
   public void handle(UniformCall call)
   {
      getTarget().handle(call);
   }

   /**
    * Returns the name of this REST connector.
    * @return The name of this REST connector.
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * Returns the description of this REST element.
    * @return The description of this REST element.
    */
   public String getDescription()
   {
      return "Jetty HTTP server";
   }
   
}
