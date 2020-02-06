# Lab2ARSW

## Integrantes:

- Andres Marcelo
- Johan Arias
- Simon Marin


# Cómo ejecutar el programa 
Además de tener instalado Maven y haberlo agreagado al path del Sistema operativo debe hacer lo siguiente: 
- Compilar el proyecto maven: ``mvn compile``
- Ejecutar el proyecto maven de manera manual: ``mvn exec:java -Dexec.mainClass=" edu.eci.arst.concprg.prodcons.StartProduction``

# Part I Start Production

Check the operation of the program and run it. While this occurs, run jVisualVM and check the CPU consumption of the corresponding process. Why is this consumption? Which is the responsible class? 

La clase responsable del consumo es la clase Consumer , debido a que se mantiene solicitando elementos de la cola siempre que tenga al menos un elemento. Sin embargo, en la clase Producer hay un margen de un segundo para agregar elementos a la cola.

![first](https://github.com/JohanS11/Lab2ARSW/blob/master/img/ars1.png)


Make the necessary adjustments so that the solution uses the CPU more efficiently, taking into account that - for now - production is slow and consumption is fast. Verify with JVisualVM that the CPU consumption is reduced.

![modified](https://github.com/JohanS11/Lab2ARSW/blob/master/img/a.png)


# Part II Synchronization and Dead-Locks.

Review the code and identify how the functionality indicated above was implemented. Given the intention of the game, an invariant should be that the sum of the life points of all players is always the same (of course, in an instant of time in which a time increase / reduction operation is not in process ). For this case, for N players, what should this value be?

El invariante para este caso radica en que la vida por defecto que le asigne al immortal deberá multiplicarse por los N jugadores, es decir, para este caso :
## N * 100.

Run the application and verify how the ‘pause and check’ option works. Is the invariant fulfilled?

No se cumple el invariante.

Check the operation again (click the button many times). Is the invariant fulfilled or not ?.

Si se cumple el invariante ahora.

