package Utilities;

public class Quintuple<A,B,C,D,E> { 
    public final A first; 
    public final B second;
    public final C third;
    public final D fourth; 
    public final E fifth;
    public Quintuple(A a, B b, C c, D d, E e) { 
      this.first = a; 
      this.second = b; 
      this.third = c;
      this.fourth = d;
      this.fifth = e;
    }
}