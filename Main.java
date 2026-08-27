import java.io.FileWriter; 
import java.io.IOException; 
import java.io.PrintWriter;

abstract class CuentaBancaria{
    private String numeroCuenta;
    private String titular;
    private double saldo;

    public CuentaBancaria (String numeroCuenta, 
        String titular, 
        double saldo){
            this.numeroCuenta=numeroCuenta;
            this.titular=titular;
            this.saldo=saldo;
    }

    public String getNumCuenta(){
        return numeroCuenta;
    }
    public void setNumCuenta(String numeroCuenta){
        this.numeroCuenta=numeroCuenta;
    }
    public String getTitular(){
        return titular;
    }
    public void setTitular(String titular){
        this.titular=titular;
    }
    public double getSaldo(){
        return saldo;
    }
    public void setSaldo(double saldo){
        this.saldo=saldo;
    }

    public void depositar(double monto){
        if (monto<=0){
            System.out.println("Error. Monto menor a 0");
            return;
        }
        saldo+=monto;
        System.out.printf(" -Deposito: $%.2f%n",monto);
    }
    public abstract void retirar(double monto);
    public abstract void comisionMensual();

    public void mostrarInfo(){
        System.out.println("| Numero de cuenta: "+numeroCuenta);
        System.out.println(" | Titular: "+titular);
        System.out.printf(" | Saldo: $%.2f%n",saldo);
    }

}

class CuentaAhorros extends CuentaBancaria{
    private double interes;
    private static double comisionMensual=10.0;

    public CuentaAhorros(String numeroCuenta, String titular, double saldo, double interes){
        super(numeroCuenta,titular,saldo);
        this.interes=interes;
    }

    public double getInteres(){
        return interes;
    }
    public void setInteres(double interes){
        if (interes>=0){
            this.interes=interes;
        }
    }

    @Override
    public void retirar(double monto){
        if (monto<=0){
            System.out.println("Error. Monto menor a 0");
            return;
        }
        if (getSaldo()>=monto){
            setSaldo(getSaldo()-monto);
            System.out.printf("- Retiro: &%.2f%n",monto);
        } else {
            System.out.println("- Retiro: Rechazado, no se permiten sobregiros.");
        }
    }
    @Override
    public void comisionMensual(){
        if (getSaldo()>=comisionMensual){
            setSaldo(getSaldo()-comisionMensual);
            System.out.printf( "- Comision mensual aplicada: $%.2f%n",comisionMensual);
        } else {
            System.out.println("- Comision mensual rechazad, por saldo insuficiente.");
        }
    }

    public void aplicarInteres(){
        double interesaplicado=getSaldo()*interes;
        setSaldo(getSaldo()+interesaplicado);
        System.out.printf( "- Interes aplicado: $%.2f%n",interesaplicado);
    }
}

class CuentaCorriente extends CuentaBancaria{
    private double cupoSobregiro; 
    private double comisionSobregiro;
    private double tasaMoraDiaria;

    public CuentaCorriente(String numeroCuenta, 
        String titular, 
        double saldo, 
        double cupoSobregiro, 
        double comisionSobregiro, 
        double tasaMoraDiaria){ 
            super(numeroCuenta, titular, saldo); 
            this.cupoSobregiro=cupoSobregiro; 
            this.comisionSobregiro=comisionSobregiro; 
            this.tasaMoraDiaria=tasaMoraDiaria; 
    }

    public double getCupoSobregiro(){ 
        return cupoSobregiro; 
    } 
    public void setCupoSobregiro(double cupoSobregiro){ 
        if (cupoSobregiro>=0){ 
            this.cupoSobregiro=cupoSobregiro; 
        } 
    } 
    
    public double getComisionSobregiro(){ 
        return comisionSobregiro; 
    } 
    public void setComisionSobregiro(double comisionSobregiro){ 
        if (comisionSobregiro>=0){ 
            this.comisionSobregiro=comisionSobregiro; 
        } 
    } 
    
    public double getTasaMoraDiaria(){ 
        return tasaMoraDiaria; 
    } 
    public void setTasaMoraDiaria(double tasaMoraDiaria){ 
        if (tasaMoraDiaria>=0){ 
            this.tasaMoraDiaria=tasaMoraDiaria; 
        } 
    }

    @Override
    public void retirar(double monto){
        if (monto<=0){
            System.out.println("Error. Monto menor a 0");
            return;
        }
        if (getSaldo()+cupoSobregiro>=monto){
            setSaldo(getSaldo()-monto);
            System.out.printf("- Retiro: &%.2f%n",monto);
        }
        if (getSaldo()<0){
            System.out.printf("- La cuenta esta en sobregiro, saldo actual: $%.2f%n",getSaldo());
        } else {
            System.out.println("- Retiro: Rechazado, excede el cupo de sobregiros.");
        }
    }


    public void retirarConMora(double monto, int diasenSobregiro){
        if(monto<=0){
            System.out.println("Error. Monto menor a 0");
            return;
        }
        if (getSaldo()+cupoSobregiro<monto){
            System.out.println("- Retiro: Rechazado, excede el cupo de sobregiros.");
            return;
        }
        setSaldo(getSaldo()-monto);
        System.out.printf("- Retiro: &%.2f%n",monto);
        if (getSaldo()<0&&diasenSobregiro>0){
            double deuda=Math.abs(getSaldo());
            double interesMora=deuda*tasaMoraDiaria*diasenSobregiro;
            setSaldo(getSaldo()-interesMora);
            System.out.printf("- Interes de mora por %d dias: $%.2f%n",diasenSobregiro,interesMora);
        }
    }

    @Override
    public void comisionMensual(){
        setSaldo(getSaldo()-comisionSobregiro);
        System.out.printf("- Comision mensual aplicada: $%.2f%n",comisionSobregiro);
    }

}

class AuditorioBancaria implements AutoCloseable{
    private PrintWriter escritor;

    public AuditorioBancaria(String nombreArch) throws IOException{
        escritor=new PrintWriter(new FileWriter(nombreArch,true));
        System.out.println("- Archivo de auditoria abierto.");
    }
    public void registro(String mensaje){
        escritor.println(mensaje);
        escritor.flush();
        System.out.println( "- Auditoria: "+mensaje);
    }
    @Override
    public void close(){
        if (escritor!=null){
            escritor.close();
            System.out.println("- Archivo de auditoria cerrado.");
        }
    }
}

/**
 * Main
 */
public class Main {
    public static void main(String[] args){
        System.out.println("| Sistema Bancario |");
        CuentaAhorros ahorro=new CuentaAhorros("123456789","Looleo",1000000.0,0.02);
        ahorro.mostrarInfo();
        CuentaCorriente corriente=new CuentaCorriente("987654321","Laura Leal",1200.0,600.0,20.0,0.01);
        corriente.mostrarInfo();
    }
    
}