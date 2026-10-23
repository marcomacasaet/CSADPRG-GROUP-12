public class Interest {
    
    double rate;
    double time;

    Interest(double rate, double time){
        this.rate = rate;
        this.time = time;
    }

    public void setIntRate(double rate){
        this.rate = rate;
    }

    public double getIntRate(){
        return this.rate;
    }

    public void setTime(double time){
        this.time = time;
    }

    public double getTime(){
        return this.time;
    }

    public double getDaily(double rate, double bal){
        return bal*(rate/365);
    }
}
