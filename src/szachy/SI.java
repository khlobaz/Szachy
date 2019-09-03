package szachy;

import java.util.Date;
import java.util.Random;
import java.util.Vector;
import szachy.WyswietlanieISterowanie.PasekPostepu;

//////////////////////////////FUNKCJE HEURYSTYCZNE///////////////////////////////////////////////////////
// GRACZ BIALY - 0 - TO GRACZ MAX
// GRACZ CZARNY - 1 - TO GRACZ MIN

class FunkcjaOceny
{
    private int licznik;
    private final int maxWartosc = Integer.MAX_VALUE;
    private final int minWartosc = Integer.MIN_VALUE;
    //////////////////////////////////////////////////
    private boolean czyOceniacPozycyjnie;
        
    FunkcjaOceny()
    {
        licznik=0;
        czyOceniacPozycyjnie = false;
    }
    
    private int ocenaMaterialna(Szachownica sz)
    {
        // wartosci poszczegolnych figur
        final int PION = 100;
        final int SKOCZEK = 300;
        final int GONIEC = 300;
        final int WIEZA = 500;
        final int HETMAN = 900;
        
        //analizujemy jakie figury znajduja sie na szachownicy
        Integer figuryGraczy[] = new Integer[13];
        for (int i=0;i<13;i++)
        {
            figuryGraczy[i]=0;
        }
        
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (sz.plansza[i][j]!=null)
                {
                    figuryGraczy[(sz.plansza[i][j].numerFigury)]++;
                }
                // 1 - bpion, 2 - bwieza, 3 - bskoczek, 4 - bgoniec, 5 - bhetman, 6 - bkrol
                // 7 - cpion, 8 - cwieza, 9 - cskoczek, 10 - cgoniec, 11 - chetman, 12 - ckrol
            }
        }
               
        
        int silaFigurBialegoGracza = figuryGraczy[1]*PION + figuryGraczy[3]*SKOCZEK + figuryGraczy[4]*GONIEC + 
                                     figuryGraczy[2]*WIEZA + figuryGraczy[5]*HETMAN;
        int silaFigurCzarnegoGracza= figuryGraczy[7]*PION + figuryGraczy[9]*SKOCZEK + figuryGraczy[10]*GONIEC + 
                                     figuryGraczy[8]*WIEZA + figuryGraczy[11]*HETMAN;
       
        return (silaFigurBialegoGracza-silaFigurCzarnegoGracza);    
    }
   
    private int ocenaPozycyjna(Szachownica sz)
    {
        
        int pozycjePionow[] = new int[2];
        pozycjePionow[0]=0; pozycjePionow[1]=0; // 0 -bialy gracz, 1 - czarny gracz
        int skoczkiNaKrawedziach[] = new int[2];
        skoczkiNaKrawedziach[0]=0; skoczkiNaKrawedziach[1]=0;
        Integer figuryGraczyWCentrum[] = new Integer[13];
        for (int i=0;i<13;i++)
        {
            figuryGraczyWCentrum[i]=0;
        }
        
        
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (sz.plansza[i][j]!=null)
                {
                    
                    //badanie pionow
                    if (sz.plansza[i][j].numerFigury == 1) // znaleziono bialego piona
                    {
                        // badamy wartosc y (w tym wypadku to i) - przesuniecie do przodu
                        pozycjePionow[0] = pozycjePionow[0] + (7 - i);
                    }
                    
                    else if (sz.plansza[i][j].numerFigury == 7) // znaleziono czarnego piona
                    {
                        pozycjePionow[1] = pozycjePionow[1] + i;
                    }
                    
                    //badanie skoczkow
                    else if (sz.plansza[i][j] instanceof Skoczek) // zanleziono skoczka
                    {
                        if ( i==0 || i==7) // skoczek znajduje sie na krawedziach y 
                        {
                            
                            if (sz.plansza[i][j].numerFigury == 3) { skoczkiNaKrawedziach[0]++; } //bialy skoczek
                            else                                   { skoczkiNaKrawedziach[1]++; }
                        }
                        if ( j==0 || j==7 ) // skoczek znajduje sie na krawedziach x
                        {
                            if (sz.plansza[i][j].numerFigury == 3) { skoczkiNaKrawedziach[0]++; }
                            else                                   { skoczkiNaKrawedziach[1]++; }
                        }
                    }
                    
                    //punkty za centrum
                    if ( (i == 3 || i == 4) && (j == 2 || j == 3 || j == 4 || j == 5) )
                    {
                        figuryGraczyWCentrum[(sz.plansza[i][j].numerFigury)]++;
                    }
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////////
        // przeliczanie zebranych danych
        final int pion1RuchDoPrzodu = 2; // 2 pky za za ruch o 1 pole do przodu
        final int skoczekNaKrawedzi = -4; // 4 pkt kary za skoczka na granicach
        // liczba dodatkowych punktow za figure w centrum
        final int centrumPion = 2; 
        final int centrumWieza = 4;
        final int centrumGoniec = 4;
        final int centrumSkoczek = 5;
        final int centrumHetman = 10;
        ///////////////////////////////////////////////////////////////////////////////
        
        // 1 - bpion, 2 - bwieza, 3 - bskoczek, 4 - bgoniec, 5 - bhetman, 6 - bkrol
        // 7 - cpion, 8 - cwieza, 9 - cskoczek, 10 - cgoniec, 11 - chetman, 12 - ckrol
        
        
        
        int silaBialegoGracza =  pozycjePionow[0]*pion1RuchDoPrzodu + skoczkiNaKrawedziach[0]*skoczekNaKrawedzi +
                                ( figuryGraczyWCentrum[1]*centrumPion + figuryGraczyWCentrum[2]*centrumWieza +
                                figuryGraczyWCentrum[3]*centrumSkoczek + figuryGraczyWCentrum[4]*centrumGoniec +
                                figuryGraczyWCentrum[5]*centrumHetman );
        
         
        int silaCzarnegoGracza = pozycjePionow[1]*pion1RuchDoPrzodu + skoczkiNaKrawedziach[1]*skoczekNaKrawedzi +
                                 ( figuryGraczyWCentrum[7]*centrumPion + figuryGraczyWCentrum[8]*centrumWieza +
                                 figuryGraczyWCentrum[9]*centrumSkoczek + figuryGraczyWCentrum[10]*centrumGoniec +
                                 figuryGraczyWCentrum[11]*centrumHetman );
               
        return silaBialegoGracza-silaCzarnegoGracza;
    }
        
///////////////////////////////////////////////////////////////////////////////// 
    public void wyzerujLicznik()
    {
        licznik=0;
    }
    public int zwrocLicznik()
    {
        return licznik;
    }
    public void zmienUstawienia(boolean oP)
    {
        czyOceniacPozycyjnie = oP;
    }
    public boolean zwrocCzyOceniacPozycyjnie()
    {
        return czyOceniacPozycyjnie;
    }
    
   
    public int dokonajOceny(Szachownica szachownica,Wezel wezel)
    {
        //uaktualniamy licznik 
        licznik++;
        
        //najpierw sprawdzamy czy moze w wezle nie jest zaznaczony koniec gry : mat, ewentualnie pat itp
        if (wezel.matKrola==true)
        {
            //sprawdzamy ktory gracz ma mata i zwracamy wartosc zakanczajac prace funkcji oceny
            if (szachownica.gracz==0)
            {
                //bialy - MAX - mat bialego - bardzo niepozadana sytuacja dla tego gracza
                return minWartosc;
            }
            else
            {
                return maxWartosc; // mat czarnego
            }
        }
        if ( (wezel.pat==true) || (wezel.trzykrotnePowtorzeniePozycji==true) || (wezel.wykonano50Posuniec == true) )
        {
            return 0; //remis czyli zwraca 0
        } 
        /////////////////////////////////////////////////////////////////////////////////////////////////
        int wynik=0;
        wynik = ocenaMaterialna(szachownica);
        
        //ocena pozycyjna - jesli zaznaczona
        if (czyOceniacPozycyjnie==true) 
        {
            int wynikOcenyPozycyjnej = ocenaPozycyjna(szachownica);
            wynik=wynik+wynikOcenyPozycyjnej;
        }
        
        /////////////////////////////////////////////////////////////////////////////////////////////////
        return wynik;
    }
}

//////////////////////////////////ANALIZOWANIE DRZEWA///////////////////////////////////////////////////

class GeneratorDrzewa 
{
    private final int maxWartosc = Integer.MAX_VALUE;
    private final int minWartosc = Integer.MIN_VALUE;
    ////////////////////////////////////////////////////////
    private FunkcjaOceny funkcjaOceny;
    private PasekPostepu pasekPostepu;
    private Date czasPracyAlgorytmu;
    private int glebokosc;
    private int nrAlgorytmu;
    private volatile Boolean flagaZakonczenia;
    private boolean czySortowac;
    private int glebokoscSortowania;
    private int matRegulatorGlebokosciDrzewa;
        
    GeneratorDrzewa(FunkcjaOceny fo,PasekPostepu pasek)
    {
        funkcjaOceny = fo;
        pasekPostepu = pasek;
        glebokosc = 6;
        nrAlgorytmu = 2;
        czasPracyAlgorytmu = new Date(0);
        flagaZakonczenia = false;
        czySortowac = true;
        glebokoscSortowania = 5;
        matRegulatorGlebokosciDrzewa = 0;
    }
         
    private int miniMax(Szachownica sz, Wezel w, int gleb) 
    {
        //gracza nie podaje bo zawarty jest w szachownicy
                
        //najpierw sprawdzamy czy glebokosc nie zeszla do 0
        if (gleb == 0)
        {
            return funkcjaOceny.dokonajOceny(sz,w);
        }
        // tworzymy dzieci wezla - wszystkie mozliwe posuniecia
        w.generujDzieci(sz,false);
        // teraz mamy policzone dla stanu 'w' czy jest szach, mat pat
        // jesli jest mat badz pat to wtedy mamy koniec gry - nie ma wiecej dzieci
        // czyli oceniamy i zwracamy wynik
        if (w.matKrola==true || w.pat==true || w.trzykrotnePowtorzeniePozycji==true || w.wykonano50Posuniec == true)
        {
            return funkcjaOceny.dokonajOceny(sz,w);
        }
        
        // teraz generujemy alg minmax dla dzieci
        if (sz.gracz==0) //czyli gracz bialy - MAX
        {
            w.wartoscFunkcjiOceny = minWartosc; // bo interesuje nas wartosc najwieksza 
            for (int i=0;i<w.dzieci.size();i++)
            {
                // musimy wykonywac ruchy - przestawiac szachownice
                int wyn = sz.wykonajRuch(w.dzieci.get(i).poprzedniRuchPrzeciwnika);
                // nalezy pamietac o promocji - jesli zajdzie algorytm podmieni figure
                if (wyn == 3) {sz.promocjaPodmienFigure( (w.dzieci.get(i).poprzedniRuchPrzeciwnika.pozycjaKoncowa),
                                                         (w.dzieci.get(i).promocjaTypFigury)); }
                
                w.dzieci.get(i).wartoscFunkcjiOceny = miniMax(sz,w.dzieci.get(i),gleb-1);
                if (w.dzieci.get(i).wartoscFunkcjiOceny > w.wartoscFunkcjiOceny) 
                {
                    w.wartoscFunkcjiOceny = w.dzieci.get(i).wartoscFunkcjiOceny;
                }
                sz.cofnijRuch();
                //cofamy ruch na szachownicy
                
                if (gleb==(glebokosc+matRegulatorGlebokosciDrzewa)) 
                { 
                    pasekPostepu.uaktualnijPasek(i+1,w.dzieci.size());
                    synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) {  break; } }
                }
                // uaktualniamy pasek postepu i sprawdzamy czy nie zazadano przerwania
            }
            if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
            // te wezly sa juz nam niepotrzebne - nalezy je skasowac bo niepotrzebnie zajmuja pamiec
            //referencje ustawiamy na null
        }
        else //czyli gracz czarny - MIN
        {
            w.wartoscFunkcjiOceny = maxWartosc;
            for (int i=0;i<w.dzieci.size();i++)
            {
                int wyn = sz.wykonajRuch(w.dzieci.get(i).poprzedniRuchPrzeciwnika);
                if (wyn == 3) {sz.promocjaPodmienFigure( (w.dzieci.get(i).poprzedniRuchPrzeciwnika.pozycjaKoncowa),
                                                         (w.dzieci.get(i).promocjaTypFigury)); }
                
                w.dzieci.get(i).wartoscFunkcjiOceny = miniMax(sz,w.dzieci.get(i),gleb-1);
                if (w.dzieci.get(i).wartoscFunkcjiOceny < w.wartoscFunkcjiOceny) 
                {
                    w.wartoscFunkcjiOceny = w.dzieci.get(i).wartoscFunkcjiOceny;
                }
                sz.cofnijRuch();
                
                if (gleb==(glebokosc+matRegulatorGlebokosciDrzewa)) 
                { 
                    pasekPostepu.uaktualnijPasek(i+1,w.dzieci.size());
                    synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) {  break; } }
                }
            }
            if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
        }
        return w.wartoscFunkcjiOceny;
        
    }
    
    private int alphaBeta(Szachownica sz, Wezel w, int gleb, int alpha, int beta) 
    {
        if (gleb == 0)
        {
            return funkcjaOceny.dokonajOceny(sz,w);
        }
        
        if (czySortowac == true)
        {
            if (gleb>((glebokosc+matRegulatorGlebokosciDrzewa)-glebokoscSortowania) ) { w.generujDzieci(sz,true); }
            else { w.generujDzieci(sz,false); }
        }
        else
        {
            w.generujDzieci(sz,false);
        }
        
        if (w.matKrola==true || w.pat==true || w.trzykrotnePowtorzeniePozycji==true || w.wykonano50Posuniec == true)
        {
            return funkcjaOceny.dokonajOceny(sz,w);
        }
        
        // teraz generujemy alpha beta dla dzieci
        if (sz.gracz==0) //czyli gracz bialy - MAX
        {
            for (int i=0;i<w.dzieci.size();i++)
            {
                
                int wyn = sz.wykonajRuch(w.dzieci.get(i).poprzedniRuchPrzeciwnika);
                if (wyn == 3) {sz.promocjaPodmienFigure( (w.dzieci.get(i).poprzedniRuchPrzeciwnika.pozycjaKoncowa),
                                                         (w.dzieci.get(i).promocjaTypFigury)); }
                
                w.dzieci.get(i).wartoscFunkcjiOceny = alphaBeta(sz,w.dzieci.get(i),gleb-1,alpha,beta);
                alpha = Math.max(w.dzieci.get(i).wartoscFunkcjiOceny,alpha);
                               
                sz.cofnijRuch();
                if (alpha >= beta) 
                {
                    if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
                    return alpha;   //odciecie !!!
                }
                
                if (gleb==(glebokosc+matRegulatorGlebokosciDrzewa)) 
                { 
                    pasekPostepu.uaktualnijPasek(i+1,w.dzieci.size()); 
                    synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) {  break; } }
                }
            }
            if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
            return alpha;
            
        }
        else //czyli gracz czarny - MIN
        {
            for (int i=0;i<w.dzieci.size();i++)
            {
                               
                int wyn = sz.wykonajRuch(w.dzieci.get(i).poprzedniRuchPrzeciwnika);
                if (wyn == 3) {sz.promocjaPodmienFigure( (w.dzieci.get(i).poprzedniRuchPrzeciwnika.pozycjaKoncowa),
                                                         (w.dzieci.get(i).promocjaTypFigury)); }
                
                w.dzieci.get(i).wartoscFunkcjiOceny = alphaBeta(sz,w.dzieci.get(i),gleb-1,alpha,beta);
                beta = Math.min(w.dzieci.get(i).wartoscFunkcjiOceny,beta);
                                
                sz.cofnijRuch();
                if (alpha >= beta) 
                {
                    if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
                    return beta;   //odciecie !!!
                }
                if (gleb==(glebokosc+matRegulatorGlebokosciDrzewa)) 
                { 
                    pasekPostepu.uaktualnijPasek(i+1,w.dzieci.size()); 
                    synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) {  break; } }
                }
            }
            if (gleb!=(glebokosc+matRegulatorGlebokosciDrzewa)) {w.dzieci=null;}
            return beta;
        }
    }
    
    private void ustawMatRegulatorGlebokosciDrzewa(int gracz, int wynik)
    {
        // manipulowanie glebokoscia generowania drzewa - jesli zostanie wykryty mat
        // sprawdzamy czy w ktoryms z wezlow wykryto mata
        if (gracz==0) // bialy gracz
        {
            if (wynik == maxWartosc) // bialy gracz wykryl, ze moze zamatowac przeciwnika
            {
                // zmniejszamy glebokosc drzewa o 2 
                matRegulatorGlebokosciDrzewa = matRegulatorGlebokosciDrzewa - 2;
            }
            else
            {
                matRegulatorGlebokosciDrzewa = 0;
            }
        }
        else // czarny gracz
        {
            if (wynik == minWartosc)
            {
                matRegulatorGlebokosciDrzewa = matRegulatorGlebokosciDrzewa - 2;
            }
            else
            {
                matRegulatorGlebokosciDrzewa = 0;
            }
        }
    }
    
    ////////////////////////////////////////////////////////
    public FunkcjaOceny zwrocFunkcjeOceny()
    {
        return funkcjaOceny;
    }
    
    public void ustawFlageZakonczenia(boolean flaga)
    {
        synchronized(flagaZakonczenia)
        {
            flagaZakonczenia=flaga;
        }
    }
    
    public void zmienUstawienia(int gleb, int nrAlg,boolean cSort, int glebSort)
    {
        glebokosc = gleb;
        nrAlgorytmu = nrAlg;
        czySortowac = cSort;
        glebokoscSortowania = glebSort;
    }
    
    public int zwrocGlebokosc()
    {
        return glebokosc;
    }
    
    public int zwrocNrAlgorytmu()
    {
        return nrAlgorytmu;
    }
    
    public boolean zwrocCzySortowac()
    {
        return czySortowac;
    }
    
    public int zwrocGlebokoscSortowania()
    {
        return glebokoscSortowania;
    }
    
    public long zwrocCzasPracyAlgorymtu()
    {
        return czasPracyAlgorytmu.getTime();
    }
    
    public void wyzerujMatRegulatorGlebokosciDrzewa()
    {
        matRegulatorGlebokosciDrzewa = 0;
    }
    
    public Wezel generujIZanalizujDrzewo(Szachownica szachownica)
    
    {
        Wezel wezel = new Wezel(new Posuniecie(0,0,0,0));
        pasekPostepu.resetujPasek(); //ustawienie paska na 0 procent
        
        Date czasPoczatek = new Date();
        int wynik=0;
        
        if (nrAlgorytmu == 1)
        {
            wynik = miniMax(szachownica,wezel,(glebokosc+matRegulatorGlebokosciDrzewa));
            
            synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) { return null;  } }
            Date czasKoniec = new Date();
            czasPracyAlgorytmu.setTime(czasKoniec.getTime()-czasPoczatek.getTime());
            
            ////////////////////////////////////////////////////
            ustawMatRegulatorGlebokosciDrzewa(szachownica.zwrocGracza(),wynik);
            //////////////////////////////////////////////////////
                      
            //robimy losowanie
            Vector <Wezel> odpowiedz = new Vector <Wezel>();  
            for (int i=0;i<wezel.dzieci.size();i++)
            {
                if (wynik == wezel.dzieci.get(i).wartoscFunkcjiOceny)
                {
                    odpowiedz.add(wezel.dzieci.get(i));
                }
            }
            int ktoryWezel = new Random().nextInt(odpowiedz.size());
            return odpowiedz.get(ktoryWezel);
        }
        
        else if (nrAlgorytmu == 2)
        {
            
            wynik = alphaBeta(szachownica,wezel,(glebokosc+matRegulatorGlebokosciDrzewa),minWartosc,maxWartosc);
            
            synchronized(flagaZakonczenia) { if(flagaZakonczenia==true) { return null; } }
            Date czasKoniec = new Date();
            czasPracyAlgorytmu.setTime(czasKoniec.getTime()-czasPoczatek.getTime());
            //ustawiamy pasek na 100 procent - alph-beta mogla dokonac odciec i nie przejrzec calosci
            pasekPostepu.uaktualnijPasek(1,1);
            
            ////////////////////////////////////////////////////
            ustawMatRegulatorGlebokosciDrzewa(szachownica.zwrocGracza(),wynik);
            //////////////////////////////////////////////////////
            
            for (int i=0;i<wezel.dzieci.size();i++)
            {
                if (wynik == wezel.dzieci.get(i).wartoscFunkcjiOceny)
                {
                    return wezel.dzieci.get(i);
                    
                }
            }
        }
           
        ///////////////////////////////////////////////////////////////////////////////
        return null;
    }
}






