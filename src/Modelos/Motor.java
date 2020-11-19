/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelos;

/**
 *
 * @author Andres Esparza
 */
public abstract class Motor {
    
    private double velocidadMaxima;

    public Motor(double velocidadMaxima) {
        this.velocidadMaxima = velocidadMaxima;
    }

    public double getVelocidadMaxima() {
        return velocidadMaxima;
    }
    
    
}
