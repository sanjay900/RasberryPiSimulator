package main;/*
 * Simbad - Robot Simulator Copyright (C) 2004 Louis Hugues
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * -----------------------------------------------------------------------------
 * $Author: sioulseuguh $
 * $Date: 2005/03/17 17:55:54 $ $Revision: 1.6 $ $Source: /cvsroot/simbad/src/simbad/demo/BaseDemo.java,v $
 */

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import simbad.demo.Demo;
import simbad.sim.*;

import java.awt.*;

/** A Basic demo with camera sensor, sonars and bumpers.
 * The robot wanders and stops when it collides.
 *
 */
public class MyArena extends Demo {
    Robot robot;
    CameraSensor camera;
    RangeSensorBelt b;
    public class Robot extends Agent {
        public Robot (Vector3d position, String name) {
            super(position,name);
            this.radius = 0.5f;
            // Add sensors
            double agentHeight = getHeight();
            float cameraBodyRadius = 0.1f;
            camera = new CameraSensor(cameraBodyRadius, 320, 240);

            camera.setUpdatePerSecond(60);
            Vector3d pos = new Vector3d(0.0, (agentHeight / 2)
                    + (cameraBodyRadius * 3) / 4+5, 0);
            addSensorDevice(camera, pos, 0);
            RangeSensorBelt b = new RangeSensorBelt(0.5f,0f,100f,4,RangeSensorBelt.TYPE_SONAR,0);

            addSensorDevice(b,pos,0);
            setKinematicModel(new DifferentialKinematic(getRadius()));
        }

        /** Initialize Agent's Behavior*/
        public void initBehavior() {
            // nothing particular in this case
        }

        /** Perform one step of Agent's Behavior */
        public void performBehavior() {
        }
        /**
         * Gets a wheels velocity.
         */
        public double getWheelsVelocity(int motor) {
            return motor == 1? ((DifferentialKinematic) kinematicModel).getLeftVelocity():((DifferentialKinematic) kinematicModel).getRightVelocity();
        }
        /**
         * Sets a wheel velocity.
         */
        public void setWheelVelocity(int motor, double value) {
            ((DifferentialKinematic) kinematicModel).setWheelsVelocity(motor==1?value:getWheelsVelocity(1), motor==2?value:getWheelsVelocity(2));
        }

    }
    public MyArena() {
        this.floorColor = new Color3f(Color.BLACK);
        this.wallColor = new Color3f(Color.WHITE);
        light1IsOn = true;
        light2IsOn = false;
        Wall w = new Wall(new Vector3d(10, 0, 0), 20, 1, this);
        w.rotate90(1);
        add(w);
        w = new Wall(new Vector3d(-10, 0, 0), 20, 1, this);
        w.rotate90(1);
        add(w);
        w = new Wall(new Vector3d(0, 0, 10), 20, 1, this);
        add(w);
        w = new Wall(new Vector3d(0, 0, -10), 20, 1, this);
        add(w);


        w = new Wall(new Vector3d(0, 0, -5), 15, 0.01f, this);
        w.collides = false;
        add(w);

        w = new Wall(new Vector3d(7, 0, -4), 2, 0.01f, this);
        w.rotateY(1.3);
        w.collides = false;
        add(w);
        robot = new Robot(new Vector3d(-5, 0, -5), "robot 1");
        add(robot);
        hasAxis = false;

    }
}