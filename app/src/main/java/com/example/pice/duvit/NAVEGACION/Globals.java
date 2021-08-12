package com.example.pice.duvit.NAVEGACION;
import java.util.ArrayList;
public class Globals {
    public static final Globals ourInstance = new Globals();
    public static Globals getInstance() { return ourInstance; }

    public int numPages = 0;
    public int idStaff = 0;
    public int idUsuario = 0;
    public int idPlaneacion = 0;
    public int numPagesAdaptar = 0;
    public int validaUbicacionGPS = 0;
    public double latitud = 0;
    public double longitud = 0;
    public float latitudWebServ = 0;
    public float longitudWebServ = 0;
    public boolean ctlBotones = false;
    public boolean ctlUbicacionCorrecta = false;
    public boolean cltZoomMap = true;
    public boolean cltPreguntaPermiso = true;
    public boolean cltPreguntarGps = true;
    public boolean cltPreguntaGpsFramentPrincipal = true;
    public boolean validaSesionIniciada = false;
    ArrayList<String> listaTareasTarea = new ArrayList<String>();
    ArrayList<String> listaTareasDescripcion = new ArrayList<String>();
    ArrayList<String> listaTareasNumero = new ArrayList<String>();
    ArrayList<String> listaTareasTareaAdapter = new ArrayList<String>();
    ArrayList<String> listaTareasDescripcionAdapter = new ArrayList<String>();
    ArrayList<String> listaTareasNumeroAdapter = new ArrayList<String>();

    ArrayList<String> listaProyectosId = new ArrayList<String>();
    ArrayList<String> listaProyectosNombre = new ArrayList<String>();
    public String idProyectoSelect = "";
    public String nombreProyectoSelect = "";

    ArrayList<String> listaIntegrantesNombre = new ArrayList<String>();
    ArrayList<String> listaIntegrantesCorreo = new ArrayList<String>();
    ArrayList<String> listaIntegrantesCelular = new ArrayList<String>();

    public boolean validaDatosDeSesion = false;

    public Globals() { }
}
