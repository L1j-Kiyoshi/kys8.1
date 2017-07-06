@echo off & color 0E
@@java -Xms1G -Xmx8G -Xincgc -cp l1jserver.jar;lib\xmlapi;lib\c3p0-0.9.1.2.jar;lib\mysql-connector-java-5.1.7-bin.jar;lib\javolution.jar;lib\JTattoo-1.6.10.jar;lib\netty-3.7.0.Final.jar;lib\org.eclipse.swt.win32.win32.x86_64_3.100.1.v4234e.jar -Dcom.sun.management.jmxremote manager.LinAllManager
@pause