package kr89.samplebor.collaudi.models;

public class TestRecordFilter {
    public enum Filter{
        ANY, YES, NO
    }
    public String licensePlate;
    public Filter mechanicsTest;
    public Filter bodyTest;
    public Filter tiresTest;
    public Filter isInsured;
}
