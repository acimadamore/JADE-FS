package pd.acimadamore.ui.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;


import java.io.File;

import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import pd.acimadamore.FileSharerApp;
import pd.acimadamore.agents.FileSharer;
import pd.acimadamore.agents.FileSharerAgent;


public class JADERuntime {

  public static JADERuntime instance;

  private Context context;
  private RuntimeServiceBinder  runtimeServiceBinder;
  private ServiceConnection     serviceConnection;

  private AgentContainerHandler agentContainerHandler;
  private AgentController       agentController;

  private FileSharer            agent = null;

  private final String services = "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService;jade.core.messaging.TopicManagementService";

  private String host;
  private String port;
  private String agentName;

  public JADERuntime(Context context) {
    this.context = context;
  }

  public void init(String host, String port, String agentName) {
    this.host      = host;
    this.port      = port;
    this.agentName = agentName;

    if (runtimeServiceBinder == null) {
      bindJADERuntimeService();
    }
    else {
      startAgentContainer();
    }
  }

  public FileSharer getAgent(){
    if(this.agent == null){
      try {
        this.agent = agentController.getO2AInterface(FileSharer.class);
      } catch (StaleProxyException e) {
        e.printStackTrace();
      }
    }

    return this.agent;
  }

  public void shutdownContainer(){
    this.agentContainerHandler.kill(new RuntimeCallback<Void>(){
      @Override
      public void onSuccess(Void aVoid) {
        JADERuntime.this.agent = null;
      }

      @Override
      public void onFailure(Throwable throwable) {}
    });
  }


  private void bindJADERuntimeService() {
    //Create Runtime Service Binder here
    serviceConnection = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName componentName, IBinder service) {
        runtimeServiceBinder = (RuntimeServiceBinder) service;
        startAgentContainer();
      }

      @Override
      public void onServiceDisconnected(ComponentName componentName) {
      }
    };

    this.context.bindService(new Intent(FileSharerApp.getAppContext(), RuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private void startAgentContainer() {
    runtimeServiceBinder.createAgentContainer(getContainerProfile(), new RuntimeCallback<AgentContainerHandler>() {
      @Override
      public void onSuccess(AgentContainerHandler agentContainerHandler) {
        JADERuntime.this.agentContainerHandler = agentContainerHandler;

        startAgent();
      }

      @Override
      public void onFailure(Throwable throwable) {
      }
    });
  }

  private void startAgent() {
    if (agentContainerHandler != null) {

      File sharedDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

      agentContainerHandler.createNewAgent(this.agentName, FileSharerAgent.class.getName(), new Object[]{sharedDirectory}, new RuntimeCallback<AgentHandler>() {
        @Override
        public void onSuccess(AgentHandler agentHandler) {
          try {
            agentHandler.getAgentController().start();

            JADERuntime.this.agentController = agentHandler.getAgentController();
          } catch (StaleProxyException e) {
            e.printStackTrace();
          }
        }

        @Override
        public void onFailure(Throwable throwable) {

        }
      });

    }
  }

  private Profile getContainerProfile(){
    Profile p = new ProfileImpl();
    p.setParameter(Profile.SERVICES,  this.services);
    p.setParameter(Profile.MAIN_HOST, this.host);
    p.setParameter(Profile.MAIN_PORT, this.port);
    p.setParameter(Profile.MAIN, Boolean.FALSE.toString());
    p.setParameter(Profile.JVM, Profile.ANDROID);

    return p;
  }
}
