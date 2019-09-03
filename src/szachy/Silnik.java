package szachy;

import java.awt.Point;
import java.util.Vector;

class Posuniecie
{
    protected Point pozycjaPoczatkowa;
    protected Point pozycjaKoncowa;
    
    Posuniecie(int pozycjaPoczatkowaX, int pozycjaPoczatkowaY,int pozycjaKoncowaX, int pozycjaKoncowaY)
    {
        pozycjaPoczatkowa = new Point(pozycjaPoczatkowaX,pozycjaPoczatkowaY);
        pozycjaKoncowa = new Point(pozycjaKoncowaX,pozycjaKoncowaY);
    }
    
    Posuniecie(Point pozPocz,Point pozKonc)
    {
        pozycjaPoczatkowa = pozPocz;
        pozycjaKoncowa = pozKonc;
    }
    
    void zmodyfikujDane(int pozycjaPoczatkowaX, int pozycjaPoczatkowaY,int pozycjaKoncowaX, int pozycjaKoncowaY)
    {
        pozycjaPoczatkowa.x = pozycjaPoczatkowaX; pozycjaPoczatkowa.y = pozycjaPoczatkowaY;
        pozycjaKoncowa.x = pozycjaKoncowaX; pozycjaKoncowa.y = pozycjaKoncowaY;
    }
    
}

class ZapisPoprzedniegoRuchu
{
    protected Posuniecie ruch;
    protected int typRuchu;
    protected Figura przechowywanaFigura;
    protected boolean zapisCzyJuzSieRuszal;
    protected int zapis50Posuniec;
    protected int numerFigury;
    
    ZapisPoprzedniegoRuchu()
    {
        
    }
    
    ZapisPoprzedniegoRuchu(Posuniecie p, int tr, Figura pf,boolean zcjsr,int z50p,int nf)
    {
        ruch = p;
        typRuchu = tr;
        przechowywanaFigura = pf;
        zapisCzyJuzSieRuszal = zcjsr;
        zapis50Posuniec = z50p;
        numerFigury = nf;
    }
    
}

abstract class Figura
{
    protected int kolorFigury;
    protected int numerFigury;
    
    Figura()
    {
        
    }
       
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        return new Vector<Point>();
    }

    Figura kopiujObiekt()
    {
        return null;
    }
}

class Pion extends Figura
{
    protected boolean czyJuzSieRuszal;
    
    Pion(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        czyJuzSieRuszal=false;
        if (kolorFigury==0) {numerFigury=1;}
        else {numerFigury=7;}
    }
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        ////////////////////////RUCH DO PRZODU////////////////////////////////////
        int doPrzoduOJednoPole;
        if (kolorFigury==0) //czyli bialy pion
        {
            doPrzoduOJednoPole=-1; // ruch bialego piona do przodu o jedno pole daje y-1
        }
        else
        {
            doPrzoduOJednoPole=1; // ruch czarnego piona do przodu o 1 pole daje y+1
        }
        
        if (szach == false) 
        {
            //sprawdzic czy mozna ruszyc sie o 1 pole na wprost
            t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
            t.y=t.y+doPrzoduOJednoPole;
            if ( t.y<=7 && t.y>=0 )  //czy nie wychodzi poza plansze 
            {
                if (plansza[t.y][t.x]==null) 
                {
                    lista.add(new Point(t.x,t.y)); 
                    //jezeli stoi jakakolwiek figura - pion nie moze sie ruszyc
            
                    //sprawdzenie warunku ruchu o dwa pola
                    if (czyJuzSieRuszal==false)
                    {
                        t.y=t.y+doPrzoduOJednoPole;
                        if ( t.y<=7 && t.y>=0 )
                        {
                            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
                        }
                    }
                }
            }
        }
        
        ///////////////////////////BICIA///////////////////////////////////////    
        
        //w lewa  i  prawa strone
        
        for (int x=-1;x<=1;x=x+2)
        {
            t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
            t.x = t.x+x; t.y = t.y+doPrzoduOJednoPole;
            if ( t.x>=0 && t.x<=7 && t.y>=0 && t.y<=7 )
            {
                if (plansza[t.y][t.x]!=null) //bicie standardowe
                {
                    if(plansza[t.y][t.x].kolorFigury!=kolorFigury) //na polu znajduje sie figura przeciwnika
                    {
                        lista.add(new Point (t.x,t.y));
                    }
                }
                else // czyli nic tam nie stoi - ale trzeba sprawdzic bicie w przelocie 
                    // - analizujac poprzedni ruch przeciwnika
                {   
                    if (szach == false)
                    {
                        //tutaj jestem ustawiony na polu bo biciu na skos (ale nie bylo tu figury przeciwnika)
                        Point poczatek = new Point();
                        Point koniec = new Point();
                
                        //bialy pojdzie do przodu o 1, czarny sie cofa o 1
                        poczatek.x = t.x;
                        poczatek.y = t.y+doPrzoduOJednoPole;
                        koniec.x = t.x;
                        koniec.y = t.y-doPrzoduOJednoPole;
                        
                        
                        if (poczatek.x == poprzedniRuchPrzeciwnika.pozycjaPoczatkowa.x &&
                            poczatek.y == poprzedniRuchPrzeciwnika.pozycjaPoczatkowa.y && 
                            koniec.x == poprzedniRuchPrzeciwnika.pozycjaKoncowa.x &&
                            koniec.y == poprzedniRuchPrzeciwnika.pozycjaKoncowa.y)
                        {
                            // skoro ruchy sie zgadzaja - nalezy jeszcze sprawdzic czy ten ruch przeciwnika 
                            // zostal wykonany pionem  - bo tylko piona on dotyczy
                            // nie ma sensu sprawdzac koloru
                            if (plansza[koniec.y][koniec.x] instanceof Pion )
                            {
                                lista.add(new Point (t.x,t.y));
                            }
                        } 
                    }
                }    
            } 
        }
        return lista;
    }
    
    Figura kopiujObiekt()
    {
        Pion p = new Pion(this.kolorFigury);
        p.czyJuzSieRuszal = this.czyJuzSieRuszal;
        p.numerFigury = this.numerFigury;
        return p;
    }
    
}

class Skoczek extends Figura
{
    Skoczek(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        if (kolorFigury==0) {numerFigury=3;}
        else {numerFigury=9;}
    }
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        //pozycja 8
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-2; t.y=t.y+1;
        if ( ((t.x)>=0) && ((t.y)<=7) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 7
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-1; t.y=t.y+2;
        if ( ((t.x)>=0) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 5
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+1; t.y=t.y+2;
        if ( ((t.x)<=7) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 4
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+2; t.y=t.y+1;
        if ( ((t.x)<=7) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 2
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+2; t.y=t.y-1;
        if ( ((t.x)<=7) && ((t.y)>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 1
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+1; t.y=t.y-2;
        if ( ((t.x)<=7) && ((t.y)>=0) )
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 11
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-1; t.y=t.y-2;
        if ( ((t.x)>=0) && ((t.y)>=0) )
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        // pozycja 10
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-2; t.y=t.y-1;
        if ( ((t.x)>=0) && ((t.y)>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        return lista;
    }

    Figura kopiujObiekt()
    {
        Skoczek s = new Skoczek(this.kolorFigury);
        s.numerFigury = this.numerFigury;
        return s;
    }

}

class Goniec extends Figura
{
    Goniec(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        if (kolorFigury==0) {numerFigury=4;}
        else {numerFigury=10;}
    }
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        //kierunek prawo/dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7&&t.y<7)
        {
            t.x++; t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek prawo/gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7&&t.y>0)
        {
            t.x++; t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo/gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0&&t.y>0)
        {
            t.x--; t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo/dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0&&t.y<7)
        {
            t.x--; t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        return lista;
    }

    Figura kopiujObiekt()
    {
        Goniec g = new Goniec(this.kolorFigury);
        g.numerFigury = this.numerFigury;
        return g;
    }

}

class Wieza extends Figura
{
    protected boolean czyJuzSieRuszal;
    
    Wieza(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        czyJuzSieRuszal=false;
        if (kolorFigury==0) {numerFigury=2;}
        else {numerFigury=8;}
    }
   
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        //kierunek prawo
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7)
        {
            t.x++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0)
        {
            t.x--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.y<7)
        {
            t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.y>0)
        {
            t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        return lista;
    }

    Figura kopiujObiekt()
    {
        Wieza w = new Wieza(this.kolorFigury);
        w.czyJuzSieRuszal = this.czyJuzSieRuszal;
        w.numerFigury = this.numerFigury;
        return w;
    }

}

class Hetman extends Figura
{
    Hetman(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        if (kolorFigury==0) {numerFigury=5;}
        else {numerFigury=11;}
    }
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        //GONIEC
        
        //kierunek prawo/dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7&&t.y<7)
        {
            t.x++; t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek prawo/gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7&&t.y>0)
        {
            t.x++; t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo/gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0&&t.y>0)
        {
            t.x--; t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo/dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0&&t.y<7)
        {
            t.x--; t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //WIEZA
        
        //kierunek prawo
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x<7)
        {
            t.x++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek lewo
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.x>0)
        {
            t.x--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek dol
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.y<7)
        {
            t.y++;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        
        //kierunek gora
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        while (t.y>0)
        {
            t.y--;
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else {
                 if (plansza[t.y][t.x].kolorFigury!=kolorFigury) { lista.add(new Point(t.x,t.y)); break; }
                 else { break; }
                 }
        }
        return lista;
    }
 
    Figura kopiujObiekt()
    {
        Hetman h = new Hetman(this.kolorFigury);
        h.numerFigury = this.numerFigury;
        return h;
    }
}

class Krol extends Figura
{
    protected boolean czyJuzSieRuszal;
    
    Krol(int kolorFigury)
    {
        this.kolorFigury = kolorFigury;
        czyJuzSieRuszal=false;
        if (kolorFigury==0) {numerFigury=6;}
        else {numerFigury=12;}
    }
    
    Vector<Point> generujMozliweRuchyFigury(Figura plansza[][],Point pozycjaFigury, Posuniecie poprzedniRuchPrzeciwnika, boolean szach)
    {
        Vector<Point> lista = new Vector<Point>();
        Point t = new Point();
        
        //sprawdzic czy krol moze sie ruszyc na ktores z bocznych pol
        
        //pozycja gora/srodek (gorny wiersz, srodkowa kolumna)
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.y=t.y-1;
        if ( ((t.y)>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja gora/prawy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+1; t.y=t.y-1;
        if ( (t.x<=7) && (t.y>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja srodek/prawy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+1;
        if ( (t.x<=7) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja dol/prawy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x+1; t.y=t.y+1;
        if ( (t.x<=7) && (t.y<=7) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja dol/srodek
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.y=t.y+1;
        if ( (t.y<=7) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja dol/lewy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-1; t.y=t.y+1;
        if ( (t.x>=0) && (t.y<=7) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja srodek/lewy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-1; 
        if ( (t.x>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        
        //pozycja gora/lewy
        t.x = pozycjaFigury.x; t.y = pozycjaFigury.y;
        t.x=t.x-1; t.y=t.y-1;
        if ( (t.x>=0) && (t.y>=0) ) 
        {
            if (plansza[t.y][t.x]==null) { lista.add(new Point(t.x,t.y)); }
            else { if (plansza[t.y][t.x].kolorFigury!=kolorFigury) lista.add(new Point(t.x,t.y)); }
        }
        /////////////////////////////ROSZADA///////////////////////////////////////////       
        
        if (szach == false)
        {
                    
            //roszada krotka - w prawo
            if (czyJuzSieRuszal==false) //badamy czy krol kiedys wczesniej wykonal ruch
            {
                //teraz nalezy sprawdzic wieze
                if (plansza[pozycjaFigury.y][7] instanceof Wieza) //sprawdzamy typ obiektu
                {
                    Wieza w = (Wieza)plansza[pozycjaFigury.y][7];
                    if (w.kolorFigury==kolorFigury) //sprawdzamy kolor wiezy
                    {
                        if (w.czyJuzSieRuszal==false) //czy wieza wczesniej nie wykonala zadnego ruchu
                        {
                            if (plansza[pozycjaFigury.y][6]==null && plansza[pozycjaFigury.y][5]==null)
                                //warunek ze pola na drodze krola i wiezy sa puste
                            {
                                lista.add(new Point(6,pozycjaFigury.y)); //krol przemieszcza sie o 2 pola !
                            }
                                           
                        }
                    }
                }
            }
        
            //roszada dluga - w lewo
            if (czyJuzSieRuszal==false) //badamy czy krol kiedys wczesniej wykonal ruch
            {
                //teraz nalezy sprawdzic wieze
                if (plansza[pozycjaFigury.y][0] instanceof Wieza) //sprawdzamy typ obiektu
                {
                    Wieza w = (Wieza)plansza[pozycjaFigury.y][0];
                    if (w.kolorFigury==kolorFigury) //sprawdzamy kolor wiezy
                    {
                        if (w.czyJuzSieRuszal==false) //czy wieza wczesniej nie wykonala zadnego ruchu
                        {
                            if (plansza[pozycjaFigury.y][1]==null && plansza[pozycjaFigury.y][2]==null
                                                                  && plansza[pozycjaFigury.y][3]==null)
                                //warunek ze pola na drodze krola i wiezy sa puste
                            {
                                lista.add(new Point(2,pozycjaFigury.y)); //krol przemieszcza sie o 2 pola !
                            }
                                           
                        }
                    }
                }
            }
        }
        return lista;
    }

    Figura kopiujObiekt()
    {
        Krol k = new Krol(this.kolorFigury);
        k.czyJuzSieRuszal = this.czyJuzSieRuszal;
        k.numerFigury = this.numerFigury;
        return k;
    }
}

class Szachownica
{
    protected Figura[][] plansza;  
    protected int gracz;
    protected Vector <ZapisPoprzedniegoRuchu> spisPoprzednichRuchow;
    protected Point pozycjaBialegoKrola;
    protected Point pozycjaCzarnegoKrola;
    protected int zapis50Posuniec;
    
    Szachownica()
    {
        plansza = new Figura[8][8];
        gracz=0;
        spisPoprzednichRuchow = new Vector <ZapisPoprzedniegoRuchu>();
        pozycjaBialegoKrola = new Point(4,7);
        pozycjaCzarnegoKrola = new Point(4,0);
        zapis50Posuniec=50;
    }
    
    void nowaGra()            
    {
        //BIALE
        plansza[7][0] = new Wieza(0);
        plansza[7][7] = new Wieza(0);
        plansza[7][1] = new Skoczek(0);
        plansza[7][6] = new Skoczek(0);
        plansza[7][2] = new Goniec(0);
        plansza[7][5] = new Goniec(0);
        plansza[7][3] = new Hetman(0);
        plansza[7][4] = new Krol(0);
        for (int i=0;i<8;i++)
        {
            plansza[6][i] = new Pion(0);
        }
        //CZARNE
        plansza[0][0] = new Wieza(1);
        plansza[0][7] = new Wieza(1);
        plansza[0][1] = new Skoczek(1);
        plansza[0][6] = new Skoczek(1);
        plansza[0][2] = new Goniec(1);
        plansza[0][5] = new Goniec(1);
        plansza[0][3] = new Hetman(1);
        plansza[0][4] = new Krol(1);
        for (int i=0;i<8;i++)
        {
            plansza[1][i] = new Pion(1);
        }
        //czyszczenie pozostalych pol
        for (int i=2;i<6;i++)
        {
            for (int j=0;j<8;j++)
            {
                plansza[i][j]=null;
            }
        }
        gracz=0;
        spisPoprzednichRuchow.clear();
        pozycjaBialegoKrola = new Point(4,7);
        pozycjaCzarnegoKrola = new Point(4,0);
        zapis50Posuniec = 50;
    }

    boolean czyJestSzach(boolean poPosunieciu)
    {
        //po posunieciu umozliwia okreslenia gracza dla jakiego mamy sprawdzac szach
        int gr;
        if (poPosunieciu==true)
        {
            if (gracz==0)
            {
                gr=1;
            }
            else //gracz = 1
            {
                gr=0;
            }
        }
        else //po posunieciu = false
        {
            gr=gracz;
        }
        
        // gracz 0 - to gracz bialy
                
        //lokalizacja naszego krola
        Point pozycjaKrola;
        if (gr == 0) // krol bialy
        {
            pozycjaKrola = pozycjaBialegoKrola;
        }
        else
        {
            pozycjaKrola = pozycjaCzarnegoKrola;
        }
       
        //////////////////////////////////TEST/////////////////////////////////
        // sprawdzenie czy tam naprawde jest krol i tego koloru co powinien 
        if (plansza[pozycjaKrola.y][pozycjaKrola.x]== null)
        {
            System.out.println("CZY JEST SZACH - krol NULL!!!");
        }
        if (plansza[pozycjaKrola.y][pozycjaKrola.x]!= null)
        {
            if (plansza[pozycjaKrola.y][pozycjaKrola.x].kolorFigury != gr)
            {
                System.out.println("CZY JEST SZACH - krol ZLY KOLOR!!!");
            }
        }
        ////////////////////////////////////////////////////////////////////////
                
        // teraz nalezy sprawdzic wszystkie pola z ktorych krol moze zostac 
        // zaatakowany - poziom, pion, skosy, a takze pozycje skoczka
        ////////////////////////////POZIOMO PIONOWO////////////////////////////
        
        //kierunek prawo
        Point t = new Point();
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x<7)
        {
            t.x++;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; } //stoi tam nasza figura
                else //czyli stoi tam figura przeciwnika
                {
                    // jezeli to bedzie hetman, badz wieza - to od razu wiadomo ze nasz krol jest szachowany
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Wieza) ) { return true;}
                    //jezeli trafimy na krola - skorzystamy z generatora posuniec
                    else if (plansza[t.y][t.x] instanceof Krol)
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                            // jesli ta figura jest w kolizji z naszym krolem - zwracamy true
                        }
                    }
                    // na tym etapie sprawdzone jest ze krol przeciwnika nie szachuje naszego krola
                    // ponadto pozostaly jeszcze pion, skoczek i goniec - ale one nie moga szachowac krola w pionie czy poziomie
                    // wiec nalezy przerwac petle
                    break;
                }
            }
        }   
            
        //kierunek lewo
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x>0)
        {
            t.x--;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Wieza) ) { return true;}
                    else if (plansza[t.y][t.x] instanceof Krol)
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }       
        
        //kierunek dol
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.y<7)
        {
            t.y++;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Wieza) ) { return true;}
                    else if (plansza[t.y][t.x] instanceof Krol)
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }      
        
        //kierunek gora
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.y>0)
        {
            t.y--;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Wieza) ) { return true;}
                    else if (plansza[t.y][t.x] instanceof Krol)
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }      
        
        //////////////////////////////SKOSY///////////////////////////////////////
        
        //kierunek prawo/dol
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x<7&&t.y<7)
        {
            t.x++; t.y++;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Goniec) ) { return true;}
                    else if ( (plansza[t.y][t.x] instanceof Krol) || (plansza[t.y][t.x] instanceof Pion) )
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                            // sprawdzamy dla piona badz krola 
                        }
                    }
                    // pion lub krol - nie powoduja naszego szacha
                    // natomiast skoczek i wieza nie moga szachowac po skosach
                    // wiec przerywamy while'a
                    break;
                }
            }
        }      
        
        //kierunek prawo/gora
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x<7&&t.y>0)
        {
            t.x++; t.y--;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Goniec) ) { return true;}
                    else if ( (plansza[t.y][t.x] instanceof Krol) || (plansza[t.y][t.x] instanceof Pion) )
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }      
       
        //kierunek lewo/gora
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x>0&&t.y>0)
        {
            t.x--; t.y--;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Goniec) ) { return true;}
                    else if ( (plansza[t.y][t.x] instanceof Krol) || (plansza[t.y][t.x] instanceof Pion) )
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }    
        
        //kierunek lewo/dol
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        while (t.x>0&&t.y<7)
        {
            t.x--; t.y++;
            if (plansza[t.y][t.x]!=null)
            {
                if(plansza[t.y][t.x].kolorFigury == gr) { break; }
                else 
                {
                    if ( (plansza[t.y][t.x] instanceof Hetman) || (plansza[t.y][t.x] instanceof Goniec) ) { return true;}
                    else if ( (plansza[t.y][t.x] instanceof Krol) || (plansza[t.y][t.x] instanceof Pion) )
                    {
                        Vector <Point> tym = plansza[t.y][t.x].generujMozliweRuchyFigury(plansza,t,null,true);
                        for (int i=0;i<tym.size();i++)
                        {
                            if (tym.get(i).x == pozycjaKrola.x && tym.get(i).y == pozycjaKrola.y) { return true; }
                        }
                    }
                    break;
                }
            }
        }    
        
        ////////////////////////////SKOCZEK////////////////////////////////////////
        
        //pozycja 8
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x-2; t.y=t.y+1;
        if ( ((t.x)>=0) && ((t.y)<=7) ) 
        {
            if (plansza[t.y][t.x]!=null) 
            { 
                if (plansza[t.y][t.x].kolorFigury!=gr)
                {
                    if (plansza[t.y][t.x] instanceof Skoczek)
                    {
                        //czyli stoi tam skoczek przeciwnika !!!
                        return true;
                    }
                }
            }
        }
        
        // pozycja 7
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x-1; t.y=t.y+2;
        if ( ((t.x)>=0) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 5
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x+1; t.y=t.y+2;
        if ( ((t.x)<=7) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 4
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x+2; t.y=t.y+1;
        if ( ((t.x)<=7) && ((t.y)<=7) )
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 2
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x+2; t.y=t.y-1;
        if ( ((t.x)<=7) && ((t.y)>=0) ) 
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 1
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x+1; t.y=t.y-2;
        if ( ((t.x)<=7) && ((t.y)>=0) )
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 11
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x-1; t.y=t.y-2;
        if ( ((t.x)>=0) && ((t.y)>=0) )
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
        
        // pozycja 10
        t.x = pozycjaKrola.x; t.y = pozycjaKrola.y;
        t.x=t.x-2; t.y=t.y-1;
        if ( ((t.x)>=0) && ((t.y)>=0) ) 
        {
            if (plansza[t.y][t.x]!=null) 
                { if (plansza[t.y][t.x].kolorFigury!=gr)
                    { if (plansza[t.y][t.x] instanceof Skoczek) { return true; }
                    }
                }
        }
 
        //jezeli przeszlismy przez wszystkie powyzsze warunki to znaczy ze nie ma szacha naszego krola czyli
        return false;
    } 
              
    void cofnijRuch()
    {
        // mamy cofnac ruch - sprawdzamy czy jest co cofac
        if (spisPoprzednichRuchow.size()>0)
        {
            ZapisPoprzedniegoRuchu spis = spisPoprzednichRuchow.remove(spisPoprzednichRuchow.size()-1);
            
            // uaktualniamy/przywracamy poprz wartosc zmiennej zapis50posuniec
            zapis50Posuniec = spis.zapis50Posuniec;
                       
            //mamy 4 typy ruchow
            if (spis.typRuchu == 0)
            // oznacza ruch standardowy - zwykle przesuniecie badz bicie      
            {
                // ustawiamy figure na pozycji poczatkowej
                plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x]=
                plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x];
                
                // teraz trzeba sprawdzic czy ta figura to pion,wieza badz krol, 
                // celem przywrocenia poprzedniej wartosci czyJuzSieRuszal;
                Figura figura = plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x];
                if (figura instanceof Pion ) { ((Pion)figura).czyJuzSieRuszal = spis.zapisCzyJuzSieRuszal; }
                else if (figura instanceof Wieza ) { ((Wieza)figura).czyJuzSieRuszal = spis.zapisCzyJuzSieRuszal; }
                else if (figura instanceof Krol ) 
                {
                    //uaktualnienie pozycji krola
                    // ruch krola - uaktualniamy pozycjaBialego/Czarnego krola
                    //krol jest juz przesuniety na poczatkowa pozycje
                    if (plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x].kolorFigury == 0) //bialy krol
                    {
                        pozycjaBialegoKrola.x = spis.ruch.pozycjaPoczatkowa.x;
                        pozycjaBialegoKrola.y = spis.ruch.pozycjaPoczatkowa.y;
                    }
                    else //czarny krol
                    {
                        pozycjaCzarnegoKrola.x = spis.ruch.pozycjaPoczatkowa.x;
                        pozycjaCzarnegoKrola.y = spis.ruch.pozycjaPoczatkowa.y;
                    }
                    
                    ((Krol)figura).czyJuzSieRuszal = spis.zapisCzyJuzSieRuszal; 
                }
                        
                //jezeli to bylo bicie to nalezy przywrocic takze poprzednia figure, ktora jest przechowywana
                //w ''przechowywanaFigura'' a jesli nie ma to ustawiamy null
                if (spis.przechowywanaFigura!=null)
                {
                    plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=spis.przechowywanaFigura;
                }
                else
                {
                    plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=null;
                }
            }
            
            else if (spis.typRuchu == 1)
            {
                //obsluga bicia w przelocie
                // najpierw cofamy piona na pozycje poczatkowa i uaktualniamy czyJuzSieRuszal
                plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x]=
                plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x];
                
                Pion pion = (Pion)plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x];
                pion.czyJuzSieRuszal=spis.zapisCzyJuzSieRuszal;
                //ustawiamy nulla na polu koncowym
                plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=null;
                
                // teraz nalezy przywrocic piona przeciwnika - stal on z boku naszej figury
                // y jest takie samo, x trzeba wyznaczyc
                if ( (spis.ruch.pozycjaKoncowa.x-spis.ruch.pozycjaPoczatkowa.x) == 1)
                // czyli bicie w przelocie bylo w prawo, -1 to w lewo
                {
                    plansza[spis.ruch.pozycjaPoczatkowa.y][(spis.ruch.pozycjaPoczatkowa.x)+1]=spis.przechowywanaFigura;
                }
                else
                {
                    plansza[spis.ruch.pozycjaPoczatkowa.y][(spis.ruch.pozycjaPoczatkowa.x)-1]=spis.przechowywanaFigura;
                }
            }
            else if(spis.typRuchu == 2)
            {
                // roszada
                // standardowo przesuwamy krola z powrotem oraz uaktualniamy czyJuzSieRuszal
                plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x]=
                plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x];
                //////////////////////////////////////////////////////////////////////
                //uaktualnienie pozycji krola
                if (plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x].kolorFigury == 0) //bialy krol
                    {
                        pozycjaBialegoKrola.x = spis.ruch.pozycjaPoczatkowa.x;
                        pozycjaBialegoKrola.y = spis.ruch.pozycjaPoczatkowa.y;
                    }
                    else //czarny krol
                    {
                        pozycjaCzarnegoKrola.x = spis.ruch.pozycjaPoczatkowa.x;
                        pozycjaCzarnegoKrola.y = spis.ruch.pozycjaPoczatkowa.y;
                    }
                /////////////////////////////////////////////////////////////////////
                Krol krol = (Krol)plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x];
                krol.czyJuzSieRuszal=false;
                //ustawiamy nulla na polu koncowym
                plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=null;
                
                // teraz nalezy przestawic wieze na swoje miejsce ale najpierw musimy sprawdzic czy
                // mamy do czynienia z lewa czy z prawa roszada, y - nie zmienia sie, badamy po x
                if ( (spis.ruch.pozycjaKoncowa.x-spis.ruch.pozycjaPoczatkowa.x) == 2)
                {
                    // roszada w prawo
                    // czyli wieza znajduje sie na polu y, 5
                    plansza[spis.ruch.pozycjaKoncowa.y][7]=plansza[spis.ruch.pozycjaKoncowa.y][5];
                    plansza[spis.ruch.pozycjaKoncowa.y][5]=null;
                    // wiadomo ze aby zaszla roszada wieza nie mogla wczesniej wykonac zadnego ruchy totez
                    // mozna z automatu ustawic czyJuzSieRuszal na false;
                    ((Wieza)plansza[spis.ruch.pozycjaKoncowa.y][7]).czyJuzSieRuszal=false;
                }
                else
                {
                    plansza[spis.ruch.pozycjaKoncowa.y][0]=plansza[spis.ruch.pozycjaKoncowa.y][3];
                    plansza[spis.ruch.pozycjaKoncowa.y][3]=null;
                    ((Wieza)plansza[spis.ruch.pozycjaKoncowa.y][0]).czyJuzSieRuszal=false;
                }
            }
            else if(spis.typRuchu == 3)
            {
                // promocja
                
                //najpierw sczytujemy kolor figury na polu koncowym
                int kolor = plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x].kolorFigury;
                //tworzymy piona
                Figura pion = new Pion(kolor);
                // ustawiamy mu czyJuzSieRuszal na true, oczywiste bo jakos musial dojsc do konca planszy
                ((Pion)pion).czyJuzSieRuszal=true;
                
                //ustawiamy naszego piona na polu poczatkowym
                plansza[spis.ruch.pozycjaPoczatkowa.y][spis.ruch.pozycjaPoczatkowa.x]=pion;
                
                // sprawdzamy czy pod przechowywanaFigura jest jakas figura
                // pion moze zrobic promocje razem z biciem !!
                if (spis.przechowywanaFigura!=null)
                {
                    plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=spis.przechowywanaFigura;
                }
                else
                {
                    plansza[spis.ruch.pozycjaKoncowa.y][spis.ruch.pozycjaKoncowa.x]=null;
                }
            }
        
            // teraz musimy przestawic gracza
            if   (gracz == 0) {gracz = 1; }
            else              {gracz = 0; }
        }
        
        
    }
    
    int wykonajRuch(Posuniecie ruch)
    {
                      
          ZapisPoprzedniegoRuchu wczesniejszyRuch = new ZapisPoprzedniegoRuchu(); 
          wczesniejszyRuch.typRuchu=0;
          // zapisujemy do wczesniejszy ruch aktualna wartosc zapis50posuniec
          wczesniejszyRuch.zapis50Posuniec = zapis50Posuniec;
          // odejmujemy 1 od aktualnej wartosci
          zapis50Posuniec = zapis50Posuniec -1;
          // jezeli dalej okaze sie ze wykonano ruch pionem badz nastapilo bicie zapis50posuniec zostanie
          // ustawione na 50;
          
          Point pocz = ruch.pozycjaPoczatkowa; 
          Point kon = ruch.pozycjaKoncowa; 
          boolean bicieWPrzelocie=false;
          boolean roszada=false;
          boolean promocja=false;
          
          ////////////////////////////////TEST////////////////////////////////////////////
          if (plansza[pocz.y][pocz.x] == null) {System.out.println("WYKONAJ RUCH - ERROR !!!!!!!!!!!!!!! NULL"); return 0; }
          if (plansza[pocz.y][pocz.x].kolorFigury != gracz) {System.out.println("WYKONAJ RUCH - ERROR !!!!!!!!!!!!!!! ZLY GRACZ"); return 0; }
          ////////////////////////////////////////////////////////////////////////////////
          
          //umieszczamy numer figury w ''wczesniejszy ruch''
          wczesniejszyRuch.numerFigury = plansza[pocz.y][pocz.x].numerFigury;
                    
          // mamy zweryfikowany ruch - mozna go wykonac
          //hetman, skoczek, goniec - nie trzeba nic modyfikowac
          if (plansza[pocz.y][pocz.x] instanceof Wieza)
          {
              Wieza w = (Wieza)plansza[pocz.y][pocz.x];
              wczesniejszyRuch.zapisCzyJuzSieRuszal=w.czyJuzSieRuszal;
              w.czyJuzSieRuszal=true;
                       
          }
          else if (plansza[pocz.y][pocz.x] instanceof Pion)
          {
              //wykonany zostanie ruch pionem czyli zapis50Posuniec=50;
              zapis50Posuniec=50;
                            
              Pion p = (Pion)plansza[pocz.y][pocz.x];
              wczesniejszyRuch.zapisCzyJuzSieRuszal=p.czyJuzSieRuszal;
              p.czyJuzSieRuszal=true;
              
              
              if ( (Math.abs(kon.y-pocz.y))==1 && (Math.abs(kon.x-pocz.x))==1 && plansza[kon.y][kon.x]==null)
              // czyli mamy do czynienia z biciem w przelocie, bo
              // mozna ruch na skos wykonac a pole docelowe jest puste
              {
                  bicieWPrzelocie=true;
              }
              
              // jezeli biale i pozycja koncowa na y = 0 to mamy promocje bialego piona
              if (p.kolorFigury==0 && kon.y==0) { promocja=true; }
              if (p.kolorFigury==1 && kon.y==7) { promocja=true; }
          }
          else if (plansza[pocz.y][pocz.x] instanceof Krol)
          {
              // ruch krola - uaktualniamy pozycjaBialego/Czarnego krola
              if (plansza[pocz.y][pocz.x].kolorFigury == 0) //bialy krol
              {
                  pozycjaBialegoKrola.x = kon.x; pozycjaBialegoKrola.y = kon.y;
              }
              else //czarny krol
              {
                  pozycjaCzarnegoKrola.x = kon.x; pozycjaCzarnegoKrola.y = kon.y;
              }
              //////////////////////////////////////////////////////////////////////////
              Krol k = (Krol)plansza[pocz.y][pocz.x];
              wczesniejszyRuch.zapisCzyJuzSieRuszal=k.czyJuzSieRuszal;
              k.czyJuzSieRuszal=true;
              
              if ( (Math.abs(kon.x-pocz.x))==2)
              // czyli mamy do czynienia z roszada
              {
                  roszada=true;
                  //zaznaczamy, ze wieza juz wykonala ruch
                  //musimy okreslic jaka to roszada 
                  if ( (pocz.x-kon.x)==-2) // czyli roszada w prawo
                  {
                      Wieza w = (Wieza)plansza[kon.y][7];
                      w.czyJuzSieRuszal=true;
                  }
                  else //czyli w lewo
                  {
                      Wieza w = (Wieza)plansza[kon.y][0];
                      w.czyJuzSieRuszal=true;
                  }
              }
          }
          
          ////////WYKONANIE RUCHU/////////
          if (gracz==0) //przestawianie - aktualizacja gracza
          {
              gracz=1;
          }
          else
          {
              gracz=0;
          }
          
          //zapisanie danych w celu uzyskania mozliwosci przyszlego cofniecia ruchu
          //zapis posuniecia
          wczesniejszyRuch.ruch = ruch;
          // zapis bitej figury - jakby byla
          if (plansza[kon.y][kon.x]!= null)
          {
              wczesniejszyRuch.przechowywanaFigura = plansza[kon.y][kon.x];
              // skoro zachowywana jest figura tzn. ze nastapilo bicie
              // ustawiamy zapis50Posuniec=50;
              zapis50Posuniec=50;
          }
          
          plansza[kon.y][kon.x] = plansza[pocz.y][pocz.x];
          plansza[pocz.y][pocz.x]=null;
          ///////////////////////////////////////////////////////////////////////////
          if (bicieWPrzelocie==true)
          {
              wczesniejszyRuch.typRuchu=1;
              wczesniejszyRuch.przechowywanaFigura = plansza[pocz.y][kon.x]; // zachowujemy tego piona
              plansza[pocz.y][kon.x]=null; // usuwamy piona przeciwnika 
          }
                   
          else if (roszada==true)
          {
              wczesniejszyRuch.typRuchu=2;
              spisPoprzednichRuchow.add(wczesniejszyRuch);
              //krol juz zostal przesuniety
              //teraz nalezy ustawic wieze
              //badamy po x
              if ( (pocz.x-kon.x)==-2)
              {
                  //wtedy mamy do czynienia z roszada krotka - w prawo
                  //wieze przesuwamy z x=7 na x=5, krol bedzie na x=6
                  plansza[kon.y][kon.x-1] = plansza[kon.y][7];
                  plansza[kon.y][7]=null;
              }
              else // dluga - wtedy byloby 2 - czyli lewo
              {
                  // wieze przesuwamy z x=0 na x=3, krol znajduje sie na x=2
                  plansza[kon.y][kon.x+1] = plansza[kon.y][0]; 
                  plansza[kon.y][0]=null;
              }
              return 2;
          }
              
          else if (promocja==true)
          {
              wczesniejszyRuch.typRuchu=3;
              spisPoprzednichRuchow.add(wczesniejszyRuch);
              return 3;
          }
          
          spisPoprzednichRuchow.add(wczesniejszyRuch);
          return 1; 
 
      //funkcja zwraca:
      // 1 - wykonany standardowy ruch
      // 2 - wykonana roszada
      // 3 - wykonany ruch z promocja
     }

    Vector<Posuniecie> generujMozliwePosuniecia()
    {
        Vector<Posuniecie> lista = new Vector<Posuniecie>();
        Posuniecie poprzedniRuchPrzeciwnika;
        if (spisPoprzednichRuchow.size()>0) { poprzedniRuchPrzeciwnika = spisPoprzednichRuchow.lastElement().ruch; }
        else { poprzedniRuchPrzeciwnika = new Posuniecie(0,0,0,0); }
        
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (plansza[i][j]!=null)
                {
                    if(plansza[i][j].kolorFigury==gracz)
                    {
                        Vector <Point> tym = new Vector<Point>();
                        Point t = new Point(j,i); // podawane w kolejnosci x,y
                        tym = plansza[i][j].generujMozliweRuchyFigury(plansza,t, poprzedniRuchPrzeciwnika,false);
                        // teraz nalezy stworzyc posuniecia i umiescic w tabeli posuniec
                        for (int a=0;a<tym.size();a++)
                        {
                            lista.add(new Posuniecie(t,tym.get(a)));
                        }
                        //nalezy zauwazyc ze w posuniecie pozycja poczatkowa caly czas umieszczamy 
                        // referencje do jednego i tego samego obiektu !!!!!!!!!!
                        
                    }
                }
            }
        }
        return lista;
        //mamy gotowa liste wszystkich mozliwych posuniec danego gracza mozliwych technicznie do wykonania
    }
    
    Szachownica kopiujObiekt()
    {
        Szachownica sz = new Szachownica();
        //najpierw kopiujemy plansze
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (plansza[i][j]!=null)
                {
                    sz.plansza[i][j] = plansza[i][j].kopiujObiekt();
                }
                else
                {
                    sz.plansza[i][j] = null;
                }
            }
        }
        // teraz pozostale pola szachownicy
        sz.gracz=gracz;
        for (int i=0;i<this.spisPoprzednichRuchow.size();i++)
        {
            ZapisPoprzedniegoRuchu ref = this.spisPoprzednichRuchow.get(i);
            
            Posuniecie r = new Posuniecie(ref.ruch.pozycjaPoczatkowa.x,ref.ruch.pozycjaPoczatkowa.y,
                                          ref.ruch.pozycjaKoncowa.x,ref.ruch.pozycjaKoncowa.y);
            int tR = ref.typRuchu;
            Figura pF = null;
            if (ref.przechowywanaFigura!=null)
            {
                pF = ref.przechowywanaFigura.kopiujObiekt();
            }
            boolean zCJSR = ref.zapisCzyJuzSieRuszal;
            int z50p = ref.zapis50Posuniec;
            int nF = ref.numerFigury;
            
            ZapisPoprzedniegoRuchu nowyObiekt = new ZapisPoprzedniegoRuchu(r,tR,pF,zCJSR,z50p,nF);
            sz.spisPoprzednichRuchow.add(nowyObiekt);
        }
        sz.pozycjaBialegoKrola = new Point(this.pozycjaBialegoKrola.x,this.pozycjaBialegoKrola.y);
        sz.pozycjaCzarnegoKrola = new Point(this.pozycjaCzarnegoKrola.x,this.pozycjaCzarnegoKrola.y);
        sz.zapis50Posuniec = this.zapis50Posuniec;
        
        return sz;  
    }
   
    void promocjaPodmienFigure(Point pozycja,int typ)
    {
        if (plansza[pozycja.y][pozycja.x]!=null)
        {
            int kolor = plansza[pozycja.y][pozycja.x].kolorFigury;
            if (typ== -1 || typ ==0) //hetman
            {
                plansza[pozycja.y][pozycja.x] = new Hetman(kolor);
            }
            
            else if (typ==1) //wieza
            {
                plansza[pozycja.y][pozycja.x] = new Wieza(kolor);
            }
            
            else if (typ==2) //skoczek
            {
                plansza[pozycja.y][pozycja.x] = new Skoczek(kolor);
            }
            
            else if (typ==3) //goniec
            {
                plansza[pozycja.y][pozycja.x] = new Goniec(kolor);
            }
        }
        
        //////////////////////////////TEST//////////////////////////////
        else
        {
            System.out.println("Podmien figure - ERROR");
        }
        ////////////////////////////////////////////////////////////////
        
    }
   
    int zwrocGracza()
    {
        return gracz;
    }
    
    int zwrocNumerFigury(int x, int y)
    {
        if (plansza[y][x]==null) //jezeli nie ma figury
        {
            return -1;
        }
        else
        {
            return plansza[y][x].numerFigury;
        }
    }
    
    int zwrocPriorytetPosuniecia(Posuniecie pos)
    {
        // mamy 3 rodzaje priorytetow 
        // 0 - najwyzszy - oznacza zbicie figury przeciwnika
        // 1 - oznacza ruch gracza do przodu - zmiana y
        // 2 - oznacza pozostale ruchy - w bok i w tyl
        
        // sprawdzamy czy na pozycji koncowej ruchu znajduje sie figura, jesli tak to mamy bicie
        if (plansza[pos.pozycjaKoncowa.y][pos.pozycjaKoncowa.x]!=null)
        {
            return 0;
        }
        // sprawdzamy czy to ruch do przodu, tutaj trzeba juz uwzglednic typ gracza
        if(gracz == 0) // bialy gracz
        {
            if( (pos.pozycjaPoczatkowa.y-pos.pozycjaKoncowa.y)>0 )
            {
                return 1;
            }
        }
        else //czarny gracz
        {
            if ( (pos.pozycjaPoczatkowa.y-pos.pozycjaKoncowa.y)<0 )
            {
                return 1;
            }
        }
        //w pozostalych przypadkach priorytet = 2
        return 2;
    }
    
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Wezel
{
    protected Vector <Wezel> dzieci;
    protected boolean szachKrola;
    protected boolean matKrola;
    protected boolean pat;
    protected Posuniecie poprzedniRuchPrzeciwnika;
    protected boolean trzykrotnePowtorzeniePozycji;
    protected boolean wykonano50Posuniec;
    
    protected int promocjaTypFigury;
    protected int wartoscFunkcjiOceny;
   
    Wezel(Posuniecie prp)
    {
        dzieci = new Vector<Wezel>();
        szachKrola=false;
        matKrola=false;
        pat=false;
        poprzedniRuchPrzeciwnika = prp;
        trzykrotnePowtorzeniePozycji=false;
        wykonano50Posuniec = false;
        
    }
    
    int generujDzieci(Szachownica szachownica,boolean czySortowac)
    {
        //otrzymujemy szachownice, bedziemy wygrnerowywac kolejne wezly wykonujac i cofajac posuniecia
        
        // najpierw badamy czy krol gracza jest szachowany przed swoim ruchem
        szachKrola = szachownica.czyJestSzach(false);
        // false - przed ruchem
        
        // badamy,czy przypadkiem nie ma remisu - poprzez trzykrotne powtorzenie pozycji kazdego z graczy
        if (szachownica.spisPoprzednichRuchow.size()>=8)
        // dopiero gdy bedzie >=8 mozna sprawdzac
        {
            //sprawdzamy czy ruch wykonywaly te same figury gracza
            int rozmiarSpisu = szachownica.spisPoprzednichRuchow.size();
            
            int fig1,fig2,fig3;
            fig1 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-8).numerFigury;
            fig2 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-6).numerFigury;
            fig3 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-2).numerFigury;
            {
                if ( (fig1 == fig2) && (fig2 == fig3) )
                {
                    //sprawdzamy czy ruch wykonywaly te same figury przeciwnika
                    fig1 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-7).numerFigury;
                    fig2 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-5).numerFigury;
                    fig3 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-1).numerFigury;
                    if ( (fig1 == fig2) && (fig2 == fig3) )
                    {
                        // sprawdzamy czy pozycja gracza jest 3 taka sama
                        Point r1,r2,r3;
                        r1 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-8).ruch.pozycjaPoczatkowa;
                        r2 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-6).ruch.pozycjaKoncowa;
                        r3 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-2).ruch.pozycjaKoncowa;
                        if ( ((r1.x == r2.x) && (r2.x == r3.x)) &&  ((r1.y == r2.y) && (r2.y == r3.y)) )
                        {
                            // sprawdzamy czy pozycja przeciwnika jest 3 taka sama
                            r1 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-7).ruch.pozycjaPoczatkowa;
                            r2 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-5).ruch.pozycjaKoncowa;
                            r3 = szachownica.spisPoprzednichRuchow.get(rozmiarSpisu-1).ruch.pozycjaKoncowa;
                            if ( ((r1.x == r2.x) && (r2.x == r3.x)) &&  ((r1.y == r2.y) && (r2.y == r3.y)) )
                            {
                            trzykrotnePowtorzeniePozycji = true;
                            }
                        }               
                    }
                }
            }
        }   
        ///////////////////////////////////////////////////////////////////////////////////////////
        // jezeli trzykrotnepowtorzeniepozycji == true nie ma sensu generowac dlaej drzewa bo
        // w tym wezle gra sie zakonczy
        
        //teraz nalezy sprawdzic czy na szachownicy wykonano 50 posuniec bez bicia lub ruchu piona
        //jesli tak to nastepuje koniec gry - nie ma po co dalej generowac drzewa
        if (szachownica.zapis50Posuniec<=0) { wykonano50Posuniec=true; }
                
        if ( (trzykrotnePowtorzeniePozycji == false) && (wykonano50Posuniec== false) )
        {
          // generujemy wszsytkie mozliwe technicznie ruchy gracza
          Vector <Posuniecie> ruchyGracza = szachownica.generujMozliwePosuniecia();
        
          //teraz wykonujemy kazdy z tych ruchow
          for (int i=0;i<ruchyGracza.size();i++)
          {
            Posuniecie ruchGracza = ruchyGracza.get(i);
            int flaga = szachownica.wykonajRuch(ruchGracza); 
            
            if (flaga == 1) // 1 - standardowy ruch
            {
                if (szachownica.czyJestSzach(true) == false)
                    {
                        // mozemy utworzyc nowy wezel 
                        dzieci.add(new Wezel(ruchGracza));
                    }
            szachownica.cofnijRuch();
            }
            
            else if (flaga == 2 && szachKrola==false) //roszade mozna wykonac tylko jesli krol nie jest szachowany !!
            // 2 - wykonywana roszada
            {
                //mamy roszade
                // aby roszada byla wykonana zgodnie z regulami gry 
                // na na wszsytkich polach po ktorych przechodzi krol nie moze byc szacha, takze tym srodkowym
                // czyli oprocz szacha przed ruchem i po ruchu trzeba takze sprawdzic pole srodkowe
                
                //sprawdzamy czy nie ma szacha po zrobieniu roszady
                boolean wynik = szachownica.czyJestSzach(true);
                // cofamy roszade
                szachownica.cofnijRuch();
                //jezeli nie bylo szacha nalezy sprawdzic czy nie bedzie go gdy przesuniemy krola o 1 pole w bok - srodkowe                
                if ( wynik == false)
                {
                    // sprawdzamy typ roszady - lewa prawa
                    int przesuniecieX;
                    if ( ((ruchGracza.pozycjaPoczatkowa.x) - (ruchGracza.pozycjaKoncowa.x)) == -2)
                    {      
                        przesuniecieX=1; //roszada w prawo
                    }
                    else
                    {
                        przesuniecieX=-1;
                    }
                
                    Posuniecie pRosz = new Posuniecie(ruchGracza.pozycjaPoczatkowa.x,ruchGracza.pozycjaPoczatkowa.y,
                                       ((ruchGracza.pozycjaPoczatkowa.x)+przesuniecieX),ruchGracza.pozycjaKoncowa.y);
                    //wykonujemy ruch 
                    szachownica.wykonajRuch(pRosz);
                    // sprawdzamy szacha
                    boolean szachSrodkowePoleRoszady = szachownica.czyJestSzach(true);
                    //po posunieciu true - bo juz sie ruszylismy i zmienilo gracza
                    // cofamy ruch
                    szachownica.cofnijRuch();
                    
                    if (szachSrodkowePoleRoszady==false)
                    {
                        // mozemy utworzyc nowy wezel 
                        dzieci.add(new Wezel(ruchGracza));
                    }
                }
            }
            else if (flaga ==2 && szachKrola ==true)
            {
                // roszada jest mozliwa technicznie do wykonania ale zachodzi szach krola
                // cofamy wykonany ruch
                szachownica.cofnijRuch();
            }
            
            else if (flaga == 3) // 3 - wykonywany ruch z promocja
            {
                //ruch zostal wykonany ale funkcja zwrocila ze jest to posuniecie z promocja
                // czyli z punktu widzenia komputera trzeba wygenerowac 4 wezly - pion zmieniony na
                // hetmana, skoczka, gonca, badz wieze
                
                //najpierw sprawdzamy szacha naszego krola po ruchu
                boolean wynik = szachownica.czyJestSzach(true);
                szachownica.cofnijRuch();
                
                if (wynik == false)
                {
                    // nalzey wygenerowac 4 wezly - bo mamy 4 miozliwosci podmiany
                    Wezel w1 = new Wezel(ruchGracza);
                    w1.promocjaTypFigury=0;
                    dzieci.add(w1);
                    
                    Wezel w2 = new Wezel(ruchGracza);
                    w2.promocjaTypFigury=1;
                    dzieci.add(w2);
                    
                    Wezel w3 = new Wezel(ruchGracza);
                    w3.promocjaTypFigury=2;
                    dzieci.add(w3);
                    
                    Wezel w4 = new Wezel(ruchGracza);
                    w4.promocjaTypFigury=3;
                    dzieci.add(w4);
                }
                
            }
          }
          // sprawdzic czy w tym wezle mamy mata lub pata
          // dzieci zawieraja wszystkie ruchy jakie gracz moze wykonac po uwgzlednieniu wszystkich regul gry
          // czyli jesli nie bedziemy mieli zadnych dzieci a nasz krol ma szach -> wynika mat
          //       jesli nie bedziemy mieli zadnych dzieci a nasz krol nie ma szach -> wynika pat
          if (dzieci.size()==0)
          {
            if (szachKrola==true)
            {
                matKrola=true;
            }
            else
            {
                pat=true;
            }
          }
        }
        ///////////////////////////////////
        //sortowanie wezlow
        if ( (czySortowac == true) && (dzieci.size()>0) ) // czy sortowanie = true i czy jest co sortowac 
        {
            Vector<Integer> priorytety = new Vector<Integer>();
            Vector <Wezel> uporzadkowaneWezly = new Vector<Wezel>();
            
            for (int q=0;q<dzieci.size();q++)
            {
                priorytety.add(szachownica.zwrocPriorytetPosuniecia(dzieci.get(q).poprzedniRuchPrzeciwnika));
            }
            
            for (int q=0;q<dzieci.size();q++) { if (priorytety.get(q) == 0) { uporzadkowaneWezly.add(dzieci.get(q)); } }
            for (int q=0;q<dzieci.size();q++) { if (priorytety.get(q) == 1) { uporzadkowaneWezly.add(dzieci.get(q)); } }
            for (int q=0;q<dzieci.size();q++) { if (priorytety.get(q) == 2) { uporzadkowaneWezly.add(dzieci.get(q)); } }
            
            // mamy uporzadkowane wezly w struktucze uporzadkowaneWezly
            // podlaczamy pod dzieci uprzadkowaneWezly
            dzieci = uporzadkowaneWezly;
            
        }
        return dzieci.size();    
    }

}

