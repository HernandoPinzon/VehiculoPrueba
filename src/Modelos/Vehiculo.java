package Modelos;

import Excepciones.AceleradoInvalidoException;
import Excepciones.ApagadoInvalidoExcepcion;
import Excepciones.EncendidoInvalidoExcepcion;
import Excepciones.FrenadoBruscoInvalidoException;
import Excepciones.FrenadoInvalidoException;
import Excepciones.LlantasInvalidasException;
import Excepciones.MotorInvalidoException;
import Excepciones.VehiculoAccidentadoException;
import Excepciones.VehiculoPantiandoException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vehiculo {

    private Motor motor;
    private Llantas llantas;
    private double velocidadActual;
    private Actualizable actualizable;
    private short estadoMotor;
    public final static short ESTADO_MOTOR_APAGADO = 0;
    public static final short ESTADO_MOTOR_ENCENDIDO = 1;
    private short estadoViaje;
    public final static short ESTADO_DETENIDO = 2;
    public final static short ESTADO_NORMAL = 3;
    public final static short ESTADO_PATINANDO = 4;
    public final static short ESTADO_ACCIDENTADO = 5;

    public Vehiculo() {
        this.velocidadActual = 0;
        this.estadoMotor = 0;
        this.estadoViaje = 2;
    }

    public void encender() throws EncendidoInvalidoExcepcion {
        //Se debe dividir en 2 if
        if (getEstadoMotor() == ESTADO_MOTOR_ENCENDIDO) {
            EncendidoInvalidoExcepcion e = new EncendidoInvalidoExcepcion("No ha sido posible encender el vehiculo, ya que el vehiculo se encuentra encendido");
            throw e;
        } else if (getEstadoViaje() == ESTADO_ACCIDENTADO) {
            EncendidoInvalidoExcepcion e = new EncendidoInvalidoExcepcion("No ha sido posible encender el vehiculo, ya que el vehiculo se encuentra accidentando");
            throw e;
        } else {
            setEstadoMotor(ESTADO_MOTOR_ENCENDIDO);
        }
    }

    public void apagar() throws ApagadoInvalidoExcepcion, VehiculoAccidentadoException {
        if (getEstadoMotor() == ESTADO_MOTOR_APAGADO) {
            ApagadoInvalidoExcepcion e = new ApagadoInvalidoExcepcion("No ha sido posible apagar el vehiculo, ya que el vehiculo se encuentra apagado");
            throw e;
        } else if (velocidadActual >= 60) {
            VehiculoAccidentadoException e = new VehiculoAccidentadoException("El vehiculo se ha accidentado, ya que se ha apagado teniedo una velocidad mayor a 60 km/h");
            accidentar();
            throw e;
        } else {
            setEstadoMotor(ESTADO_MOTOR_APAGADO);
            setEstadoViaje(ESTADO_DETENIDO);
            velocidadActual = 0;
        }
    }

    public void acelerar(int velocidad) throws AceleradoInvalidoException, VehiculoAccidentadoException {
        double velocidadFinal = getVelocidadActual() + velocidad;
        while (velocidadFinal > getVelocidadActual()) {
            if (velocidad > 0) {
                setEstadoViaje(ESTADO_NORMAL);
            }
            if (estadoMotor == ESTADO_MOTOR_APAGADO) {
                AceleradoInvalidoException e = new AceleradoInvalidoException("No ha sido posible acelerar, ya que el vehiculo se encuentra apagado");
                throw e;
            } else if (getEstadoViaje() == ESTADO_PATINANDO) {
                AceleradoInvalidoException e = new AceleradoInvalidoException("No ha sido posible acelerar, ya que el vehiculo se encuentra patinando");
                throw e;
            } else if (getEstadoViaje() == ESTADO_ACCIDENTADO) {
                AceleradoInvalidoException e = new AceleradoInvalidoException("No ha sido posible acelerar, ya que el vehiculo se encuentra accidentado");
                throw e;
            } else if (velocidadActual > motor.getVelocidadMaxima() - 1) {
                VehiculoAccidentadoException e = new VehiculoAccidentadoException("El vehiculo se ha accidentado, ya que este se acelero mas alla de la capacidad de su motor, la cual es de " + motor.getVelocidadMaxima());
                System.out.println("Supere mi motor");
                accidentar();
                throw e;
            } else {
                velocidadActual++;
                try {
                    Thread.sleep(200);
                    actualizable.actualizar();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Vehiculo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void frenar(int velocidad) throws FrenadoInvalidoException, VehiculoPantiandoException, FrenadoBruscoInvalidoException {
        double velocidadFinal = getVelocidadActual() + velocidad;
        if (estadoMotor == ESTADO_MOTOR_APAGADO) {
            FrenadoInvalidoException e = new FrenadoInvalidoException("No ha sido posible frenar, ya que el vehiculo se encuentra apagado");
            throw e;
        }
        if (-velocidad > velocidadActual) {
            VehiculoPantiandoException e = new VehiculoPantiandoException("El vehiculo patino, ya que se freno con una intensidad mayor a su velocidad");
            patinar();
            throw e;
        }

        if (-velocidad >= 30) {
            frenarBruscamente(velocidad);
        } else {
            while (velocidadFinal < getVelocidadActual()) {
                if (getEstadoViaje() == ESTADO_DETENIDO) {
                    FrenadoInvalidoException e = new FrenadoInvalidoException("No ha sido posible frenar, ya que el vehiculo se encuentra detenido");
                    throw e;
                } else if (getEstadoViaje() == ESTADO_PATINANDO) {
                    FrenadoInvalidoException e = new FrenadoInvalidoException("No ha sido posible frenar, ya que el vehiculo se encuentra patinando");
                    throw e;
                } else if (getEstadoViaje() == ESTADO_ACCIDENTADO) {
                    FrenadoInvalidoException e = new FrenadoInvalidoException("No ha sido posible frenar, ya que el vehiculo se encuentra accidentado");
                    throw e;
                } else {
                    velocidadActual--;
                    System.out.println(getVelocidadActual());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Vehiculo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (velocidadActual <= 0) {
                    setEstadoViaje(ESTADO_DETENIDO);
                }
                actualizable.actualizar();
            }
        }
        actualizable.actualizar();
    }

    public void frenarBruscamente(int velocidad) throws FrenadoBruscoInvalidoException, VehiculoPantiandoException {
        if (getEstadoMotor() == ESTADO_MOTOR_APAGADO) {
            FrenadoBruscoInvalidoException e = new FrenadoBruscoInvalidoException("No ha sido posible frenar bruscamente, ya que el vehiculo se encuentra apagado");
            throw e;
        } else if (getEstadoViaje() == ESTADO_DETENIDO) {
            FrenadoBruscoInvalidoException e = new FrenadoBruscoInvalidoException("No ha sido posible frenar bruscamente, ya que el vehiculo se encuentra detenido");
            throw e;
        } else if (getEstadoViaje() == ESTADO_PATINANDO) {
            FrenadoBruscoInvalidoException e = new FrenadoBruscoInvalidoException("No ha sido posible frenar bruscamente, ya que el vehiculo se encuentra patinando");
            throw e;
        } else if (getEstadoViaje() == ESTADO_ACCIDENTADO) {
            FrenadoBruscoInvalidoException e = new FrenadoBruscoInvalidoException("No ha sido posible frenar bruscamente, ya que el vehiculo se encuentra accidentado");
            throw e;
        } else if (velocidadActual > llantas.getVelocidadMaxima()) {
            VehiculoPantiandoException e = new VehiculoPantiandoException("El vehiculo patino, ya que este freno bruscamente , y su velocidad era superior a la permitida por sus llantas(" + llantas.getVelocidadMaxima() + "Km/h)");
            patinar();
            System.out.println("Patine");
            throw e;
        } else {
            System.out.println(velocidad + " " + llantas.getVelocidadMaxima());
            velocidadActual = velocidadActual + velocidad;
        }
        actualizable.actualizar();
    }

    public void setEstadoMotor(short estadoMotor) {
        this.estadoMotor = estadoMotor;
    }

    public void setEstadoViaje(short estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public void addComponents(String[] l, String[] m) throws LlantasInvalidasException, MotorInvalidoException {
        if (!l[0].equals("llantas")) {
            LlantasInvalidasException e = new LlantasInvalidasException("Archivo de texto tiene contenido inadecuado");
            throw e;
        } else if (!l[1].equals("Bonitas") && !l[1].equals("Baratas") && !l[1].equals("Buenas")) {
            LlantasInvalidasException e = new LlantasInvalidasException("Tipo de llantas invalido para este simulador");
            throw e;
        } else if (!m[0].equals("motor")) {
            MotorInvalidoException e = new MotorInvalidoException("Archivo de texto tiene contenido inadecuado");
            throw e;
        } else if (!m[1].equals("1000") && !m[1].equals("2000") && !m[1].equals("3000")) {
            MotorInvalidoException e = new MotorInvalidoException("Cilindraje del motor invalido para este simulador");
            throw e;
        } else {
            Llantas llantas;
            Motor motor;
            if (l[1].equals("Bonitas")) {
                llantas = new LlantasBonitas();
            } else if (l[1].equals("Buenas")) {
                llantas = new LlantasBuenas();
            } else {
                llantas = new LlantasBaratas();
            }
            if (m[1].equals("1000")) {
                motor = new Motor1000cc();
            } else if (m[1].equals("2000")) {
                motor = new Motor2000cc();
            } else {
                motor = new Motor3000cc();
            }
            setMotor(motor);
            setLlantas(llantas);
        }
    }

    public double getVelocidadActual() {
        return velocidadActual;
    }

    public short getEstadoMotor() {
        return estadoMotor;
    }

    public short getEstadoViaje() {
        return estadoViaje;
    }

    public void setMotor(Motor motor) {
        this.motor = motor;
    }

    public void setLlantas(Llantas llantas) {
        this.llantas = llantas;
    }

    public Actualizable getActualizable() {
        return actualizable;
    }

    public void setActualizable(Actualizable actualizable) {
        this.actualizable = actualizable;
    }

    public void patinar() {
        setEstadoViaje(ESTADO_PATINANDO);
        actualizable.actualizar();
        System.out.println("Patine");
        while (velocidadActual > 5) {
            velocidadActual -= 5;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        velocidadActual = 0;
        setEstadoViaje(ESTADO_DETENIDO);
        actualizable.actualizar();
    }

    public void accidentar() {
        setEstadoMotor(ESTADO_MOTOR_APAGADO);
        setEstadoViaje(ESTADO_ACCIDENTADO);
        velocidadActual = 0;
        actualizable.actualizar();
    }
}
