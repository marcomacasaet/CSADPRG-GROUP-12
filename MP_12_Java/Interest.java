public class Interest {
    
    float rate;
    int time;

    Interest(){
        this.rate = 0.05f;
        this.time = 0;
    }

    public void setIntRate(float rate){
        this.rate = rate;
    }

    public float getIntRate(){
        return this.rate;
    }

    public void setTime(int time){
        this.time = time;
    }

    public float getTime(){
        return this.time;
    }

    public float getDaily(float rate, float bal){
        return bal*(rate/365);
    }
}
