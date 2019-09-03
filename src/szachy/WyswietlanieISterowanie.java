package szachy;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
//////////////////////////////////////////////////////////////////////////////////////////////////
class TrybPrzegladania
{
    protected Vector<Posuniecie> posuniecia;
    protected Vector<Integer> promocja;
    protected int numerRuchu;
    protected Vector<Long> liczbaOcenionychLisci;

    TrybPrzegladania()
    {
        posuniecia = new Vector<Posuniecie>();
        promocja = new Vector<Integer>();
        numerRuchu=0;
        liczbaOcenionychLisci = new Vector<Long>();
    }

    public void dodajRuch(Posuniecie pos, int prom,long liscie)
    {
        posuniecia.add(pos);
        promocja.add(prom);
        numerRuchu++;
        liczbaOcenionychLisci.add(liscie);
    
    }

    public void usunRuch()
    {
        if (numerRuchu>0)
        {
            posuniecia.remove(numerRuchu-1);
            promocja.remove(numerRuchu-1);
            liczbaOcenionychLisci.remove(numerRuchu-1);
            numerRuchu--;
        }
    }
}

abstract class Zegar extends Thread
{
    private Date czasAktualny;
    private Date czasPrzedPosunieciem;
    
    private volatile Date czasCalkowity;
    private boolean pierwszyRaz;
    private volatile Integer flagaSterujaca;
    private int odstepCzasu;
    private volatile Boolean flagaZakonczenia;

    Zegar(int odstep)
    {
        czasCalkowity = new Date(0);
        pierwszyRaz=true;
        flagaSterujaca = 0;
        odstepCzasu = odstep;
        flagaZakonczenia=false;
    }
    
    public void wlacz()
    {
        synchronized(flagaSterujaca)
        {
            flagaSterujaca = 1;
        }
    }
    
    public void wylacz()
    {
        synchronized(flagaSterujaca)
        {
            flagaSterujaca = 0;
        }
    }
    
    public Date zakonczPraceIZwrocCzas()
    {
        synchronized(flagaZakonczenia) 
        //ustawiamy flage zakonczenia - wtedy w run nastapi zakonczenie petli for(;;)
        {
            flagaZakonczenia=true;
        }
        synchronized (flagaSterujaca)
        // przestawiamy flage sterujaca na 0 aby nastapilo wskoczenie w warunek 0
        {
            flagaSterujaca=0;
        }
        Date data;
        synchronized(czasCalkowity)
        // czekamy az obiekt czas calkowity bedzie dostepny - a bedzie gdy wyjdziemy z petli for(;;)
        // potem zwracamy czas calkowity
        {
            data = czasCalkowity;
        }
        
    return data;       
    }
    
    protected void aktualizujCzas(String czas)
    {
        
    }
    
    public void run() 
    {
        for(;;)
        {
            synchronized(czasCalkowity)
            {
                int kopiaFlagi;
                synchronized(flagaSterujaca)
                {
                    kopiaFlagi = flagaSterujaca;
                }
                if (kopiaFlagi == 1)
                {
                    if (pierwszyRaz==true)
                    {
                        czasPrzedPosunieciem = new Date();
                        pierwszyRaz=false;
                    }
                    czasAktualny= new Date();
                    Date tym = new Date();
                    tym.setTime( czasCalkowity.getTime() + (czasAktualny.getTime() - czasPrzedPosunieciem.getTime()) );
                    
                    long czasWMilisekundach = tym.getTime();
                    String czasGracza="";
                    long czasWSekundach = czasWMilisekundach/1000;
             
                    long godziny = czasWSekundach/3600;
                    long minuty = (czasWSekundach/60)%60;
                    long sekundy = czasWSekundach % 60;
            
                    if (godziny<10) {czasGracza=czasGracza+"0";}
                    czasGracza=czasGracza+godziny+":";
                    if (minuty<10) {czasGracza=czasGracza+"0";}
                    czasGracza=czasGracza+minuty+":";
                    if (sekundy<10) {czasGracza=czasGracza+"0";}
                    czasGracza=czasGracza+sekundy;
                  
                    aktualizujCzas(czasGracza);
                }
            
                if (kopiaFlagi == 0) //czyli mamy 0 - stop !!!
                {
                    pierwszyRaz=true;
                    if (czasAktualny!=null && czasPrzedPosunieciem!=null)
                    {
                        czasCalkowity.setTime( czasCalkowity.getTime() + (czasAktualny.getTime() - czasPrzedPosunieciem.getTime()) );
                    }
                    czasAktualny=null;
                    czasPrzedPosunieciem=null;
                
                    synchronized(flagaZakonczenia) // sprawdzamy warunek wyjscia - przerwania fora (;;)
                    {
                        if (flagaZakonczenia==true)
                        {
                            break;
                        }
                    }
                }
                try { sleep(odstepCzasu); }    
                catch (InterruptedException ex) { Logger.getLogger(Zegar.class.getName()).log(Level.SEVERE, null, ex); }
            }
        }
    }
}   
//////////////////////////////////////////////////////////////////////////////////////////////////

public class WyswietlanieISterowanie extends javax.swing.JFrame 
{
    class ZegarBialegoGracza extends Zegar
    {
        ZegarBialegoGracza(int odstep)
        {
            super(odstep); //wywolujemy konstruktor z nadklasy
        }
        
        protected void aktualizujCzas(String czas)
        {
            jLabel1.setText(czas);  
        }
    }
    
    class ZegarCzarnegoGracza extends Zegar
    {
        ZegarCzarnegoGracza(int odstep)
        {
            super(odstep);
        }
        
        protected void aktualizujCzas(String czas)
        {
            jLabel2.setText(czas);            
        }
    }

    class Wyswietlacz extends JPanel
    {
    
        Wyswietlacz(int rs)
        {
            super();
            this.setPreferredSize(new Dimension(rs,rs));
                      
        }

        public void paint(Graphics g)
        {
            // trzyma wcisniety przycisk myszy i przeciaga
            if (zaznaczonePolePoczatkowe.x!=(-1) && zaznaczonePolePoczatkowe.y!=(-1))
            {
                g.drawImage(obrazy[0],0,0,null);
                for (int i=0;i<8;i++)
                {
                    for (int j=0;j<8;j++)
                    {
                        if (!((zaznaczonePolePoczatkowe.x==j)&&(zaznaczonePolePoczatkowe.y==i)))
                        {
                            int numerFigury = szachownica.zwrocNumerFigury(j,i);
                            if (numerFigury!= -1)
                            {
                            g.drawImage(obrazy[numerFigury],ramkaSzachownicy+j*rozmiarPolaNaSzachownicy,
                                                            ramkaSzachownicy+i*rozmiarPolaNaSzachownicy,null);    
                            }
                        }
                    }
                }
                // tutaj rysowana jest figura przyczepiona do myszki
                int numerFigury = szachownica.zwrocNumerFigury(zaznaczonePolePoczatkowe.x,zaznaczonePolePoczatkowe.y);
                if (numerFigury!= -1)
                {
                    g.drawImage(obrazy[numerFigury],wspolrzedneMyszy.x-rozmiarPolaNaSzachownicy/2,
                                wspolrzedneMyszy.y-rozmiarPolaNaSzachownicy/2,null);
                }
            }
            else //w pozostalych przypadkach
            {
                g.drawImage(obrazy[0],0,0,null);
                for (int i=0;i<8;i++)
                {
                    for (int j=0;j<8;j++)
                    {
                        int numerFigury = szachownica.zwrocNumerFigury(j,i);
                        if (numerFigury!= -1)
                        {
                        g.drawImage(obrazy[numerFigury],ramkaSzachownicy+j*rozmiarPolaNaSzachownicy,
                                    ramkaSzachownicy+i*rozmiarPolaNaSzachownicy,null);
                        }
                    }
                }
            } 
        }
    }
    
    class PasekPostepu 
    {
        public void uaktualnijPasek(int ktoryWezel, int liczbaWszystkichWezlow)
        {
            int wartoscPaska =  (int) ( ( ((double)ktoryWezel) / ((double)liczbaWszystkichWezlow) ) * 100.0 );
            jProgressBar1.setValue(wartoscPaska);
        }
        
        public void resetujPasek()
        {
            jProgressBar1.setValue(0);
        }
    }
    
    class GeneratorPosuniecSI extends Thread
    {
                
        public void run()
        {
            Szachownica sz = szachownica.kopiujObiekt();
            Wezel wezel;
            if (szachownica.zwrocGracza()==0) 
            { 
                SIBialy.zwrocFunkcjeOceny().wyzerujLicznik();
                jMenuItem4.setEnabled(true);
                wezel = SIBialy.generujIZanalizujDrzewo(sz);
                jMenuItem4.setEnabled(false);
            } 
            else
            {
                SICzarny.zwrocFunkcjeOceny().wyzerujLicznik();
                jMenuItem4.setEnabled(true);
                wezel = SICzarny.generujIZanalizujDrzewo(sz);
                jMenuItem4.setEnabled(false);
            }
            
            if (wezel!=null)
            {
                if (szachownica.zwrocGracza()==0) //bialy gracz
                {
                    
                    /////////////////////////////////TEST////////////////////////////////////////////
                    testujSzachownice(szachownica,sz);
                    ////////////////////////////////////////////////////////////////////////////////
                    
                    int liczbaOcen = SIBialy.zwrocFunkcjeOceny().zwrocLicznik();
                    long czasPracy = SIBialy.zwrocCzasPracyAlgorymtu();
               
                    konsola.setText(konsola.getText() + "CZAS: " + czasPracy + " ms. " + "OCENIONO: " + liczbaOcen 
                                    + " LIŚCI.\n");
                    konsola.setText(konsola.getText() + (String.valueOf(trybPrzegladania.numerRuchu+1)) +". BG" + 
                                    "   " +przeksztalcWspolrzedneDoStringa(wezel.poprzedniRuchPrzeciwnika) + "\n");
                              
                    int wynik = szachownica.wykonajRuch(wezel.poprzedniRuchPrzeciwnika);
                    if (wynik != 3) { trybPrzegladania.dodajRuch(wezel.poprzedniRuchPrzeciwnika,0,liczbaOcen); }
               
                    if (wynik == 3)
                    {
                        trybPrzegladania.dodajRuch(wezel.poprzedniRuchPrzeciwnika,wezel.promocjaTypFigury,liczbaOcen);
                   
                        szachownica.promocjaPodmienFigure(wezel.poprzedniRuchPrzeciwnika.pozycjaKoncowa,wezel.promocjaTypFigury);
                    }
                    jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu)); //uaktualnienie numeru ruchu
                }
                else
                {
                    
                    /////////////////////////////////TEST////////////////////////////////////////////
                    testujSzachownice(szachownica,sz);
                    ////////////////////////////////////////////////////////////////////////////////
                                    
                    int liczbaOcen = SICzarny.zwrocFunkcjeOceny().zwrocLicznik();
                    long czasPracy = SICzarny.zwrocCzasPracyAlgorymtu();
                
                    konsola.setText(konsola.getText() + "CZAS: " + czasPracy + " ms. " + "OCENIONO: " + liczbaOcen 
                                    + " LIŚCI.\n");
                    konsola.setText(konsola.getText() + (String.valueOf(trybPrzegladania.numerRuchu+1)) +". CG" + 
                                    "   " +przeksztalcWspolrzedneDoStringa(wezel.poprzedniRuchPrzeciwnika) + "\n");
                
                    int wynik = szachownica.wykonajRuch(wezel.poprzedniRuchPrzeciwnika);
                    if (wynik != 3) { trybPrzegladania.dodajRuch(wezel.poprzedniRuchPrzeciwnika,0,liczbaOcen); }
                
                    if (wynik == 3)
                    {
                        trybPrzegladania.dodajRuch(wezel.poprzedniRuchPrzeciwnika,wezel.promocjaTypFigury,liczbaOcen); 
                    
                        szachownica.promocjaPodmienFigure(wezel.poprzedniRuchPrzeciwnika.pozycjaKoncowa,wezel.promocjaTypFigury);
                    }
                    jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu)); //uaktualnienie numeru ruchu
                }
                jPanel1.repaint();
                konsola.setCaretPosition(konsola.getDocument().getLength());
                sterowanieGra();
            }
            else // czyli przerwano obliczenia bo wezel = null
            {
                jMenuItem1.setEnabled(true); // odbezpiecz nowa gra
                jMenuItem5.setEnabled(true); //wznow obliczenia
                jMenuItem8.setEnabled(true); // dostep do Sztucznej Inteligencji
                konsola.setText(konsola.getText() + "Przerwano obliczenia!\n");
                konsola.setCaretPosition(konsola.getDocument().getLength());
            }
        }
        
    }
    
    ///////////////////////////////////////////////////////////////        
    private Szachownica szachownica;
    private final static int rozmiarPolaNaSzachownicy = 44;
    private final static int rozmiarSzachownicy = 382;
    private final static int ramkaSzachownicy = 15;
    private BufferedImage obrazy[];
    private String nazwyGrafik[];
    private ZegarBialegoGracza zegarBialegoGracza;
    private ZegarCzarnegoGracza zegarCzarnegoGracza;
    ///////////////////////////////////////////////////////////////
    private Point wspolrzedneMyszy;
    private Point zaznaczonePolePoczatkowe;
    private Point zaznaczonePoleKoncowe;
    ///////////////////////////////////////////////////////////////
    private boolean blokadaMyszy;
    private int ktoGraBialyGracz;    // 0 - gra komputer, 1 - czlowiek
    private int ktoGraCzarnyGracz;
    
    private GeneratorDrzewa SIBialy;
    private GeneratorDrzewa SICzarny;
    
    private TrybPrzegladania trybPrzegladania;
    private PasekPostepu pasekPostepu;
    ////////////////////////////////////////////////////////////////////////////////////
    
    
    public WyswietlanieISterowanie() 
    {
        initComponents();
        obrazy = new BufferedImage[13];
        nazwyGrafik = new String[13];
        wczytajObrazy();
        szachownica = new Szachownica();
        szachownica.nowaGra();
        blokadaMyszy = true;
        this.setIconImage(obrazy[3]);
        this.setTitle(" SZACHY");
        
        ktoGraBialyGracz = 1;
        ktoGraCzarnyGracz = 1;
        wspolrzedneMyszy = new Point(0,0);
        zaznaczonePolePoczatkowe = new Point(-1,-1);
        zaznaczonePoleKoncowe = new Point(0,0);
        
        pasekPostepu = new PasekPostepu();
        SIBialy = new GeneratorDrzewa(new FunkcjaOceny(),pasekPostepu);
        SICzarny = new GeneratorDrzewa(new FunkcjaOceny(),pasekPostepu);
        
        //////////////////////////////////////////////////////
        jButton1.setEnabled(false);
        jButton3.setEnabled(false);
        jMenuItem3.setEnabled(false);
        jMenuItem4.setEnabled(false);
        jMenuItem5.setEnabled(false);
        
        
    }
    
    private void nowaGra()
    {
        Toolkit.getDefaultToolkit().beep();
        Object opcje[] ={"GRACZ VS GRACZ","BIAŁY GRACZ VS KOMPUTER","CZARNY GRACZ VS KOMPUTER","KOMPUTER VS KOMPUTER"};
        int odpowiedz = JOptionPane.showOptionDialog(this," Wybierz rodzaj rozgrywki.","NOWA GRA",
                        JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE, null,opcje,opcje[0]);
        //-1 - x , 0 - gr vs gr, 1- bgr vs kom, 2 - cgr vs kom, 3 - kom vs kom
        if (odpowiedz!= -1)
        {
            szachownica.nowaGra();
            blokadaMyszy=true;
            if      (odpowiedz==0) {ktoGraBialyGracz = 1; ktoGraCzarnyGracz = 1; } 
            else if (odpowiedz==1) {ktoGraBialyGracz = 1; ktoGraCzarnyGracz = 0; }
            else if (odpowiedz==2) {ktoGraBialyGracz = 0; ktoGraCzarnyGracz = 1; }
            else if (odpowiedz==3) {ktoGraBialyGracz = 0; ktoGraCzarnyGracz = 0; }
            wspolrzedneMyszy = new Point(0,0);
            zaznaczonePolePoczatkowe = new Point(-1,-1);
            zaznaczonePoleKoncowe = new Point(0,0);
            //tworzymy i uruchamiamy zegary 
            if (zegarBialegoGracza != null) { zegarBialegoGracza.zakonczPraceIZwrocCzas(); }
            if (zegarCzarnegoGracza != null) { zegarCzarnegoGracza.zakonczPraceIZwrocCzas(); }
            zegarBialegoGracza = new ZegarBialegoGracza(25);
            zegarCzarnegoGracza = new ZegarCzarnegoGracza(25);
            zegarBialegoGracza.aktualizujCzas("00:00:00");
            zegarCzarnegoGracza.aktualizujCzas("00:00:00");
            zegarBialegoGracza.start();
            zegarCzarnegoGracza.start();
            trybPrzegladania = new TrybPrzegladania();
            jPanel1.repaint();
            
            jButton1.setEnabled(false);
            jButton3.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jMenuItem4.setEnabled(false); //przerwij obliczenia
            jMenuItem5.setEnabled(false); //wznow obliczenia
            jLabel3.setText("0");
            konsola.setText("");
            pasekPostepu.resetujPasek();
            SIBialy.ustawFlageZakonczenia(false);
            SICzarny.ustawFlageZakonczenia(false);
            SIBialy.wyzerujMatRegulatorGlebokosciDrzewa();
            SICzarny.wyzerujMatRegulatorGlebokosciDrzewa();
            sterowanieGra();
            
        }
        
    }
 
    private int sprawdzWarunkiKoncoweGry(Szachownica sz, Wezel sR,boolean glosnyTrybPracy)
    {
        int czyKoniecGry=0;
        String kolorGracza[] = new String[2];
        if (sz.gracz == 0) { kolorGracza[0] = "Biały"; kolorGracza[1]="Czarny";}
        else            { kolorGracza[0] = "Czarny"; kolorGracza[1]="Bialy";}
        
        if (sR.matKrola==false && sR.pat==false)
        {
            if (sR.trzykrotnePowtorzeniePozycji==true)
            {
                czyKoniecGry=3;
                if (glosnyTrybPracy==true)
                {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this,"Trzykrotne powtórzenie pozycji!","REMIS",JOptionPane.INFORMATION_MESSAGE);
            
                }
            }
                    
            else if (sR.wykonano50Posuniec==true)
            {
                czyKoniecGry=4;
                if (glosnyTrybPracy==true)
                {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this,"Przez 50 ruchów nie wykonano bicia ani ruchu pionem!","REMIS",JOptionPane.INFORMATION_MESSAGE);
                }
            }  
        }
        else
        {
            if (sR.matKrola==true)
            {
                czyKoniecGry=1;
                if (glosnyTrybPracy==true)
                {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this,kolorGracza[0] + " król został zamatowany.","Wygrywa " + kolorGracza[1] + " gracz!",JOptionPane.WARNING_MESSAGE);
                }
            }
            else if (sR.pat==true)
            {
                czyKoniecGry=2;
                if (glosnyTrybPracy==true)
                {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this,kolorGracza[0] + " król - PAT!","REMIS",JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
        // 0 - nie ma konca gry, 1-mat, 2-pat, 3-3powtorzenia, 4-50posuniec
        return czyKoniecGry;
    }
    
    private void koniecGry(int ktoryGracz,int koniecGry)
    {
        zegarBialegoGracza.zakonczPraceIZwrocCzas();
        zegarCzarnegoGracza.zakonczPraceIZwrocCzas();
        jMenuItem3.setEnabled(false); //cofnij ruch wylaczone
        jButton1.setEnabled(true); //przycisk cofnij wlaczony
        jMenuItem8.setEnabled(true); // dostep do Sztucznej Inteligencji 
        jMenuItem1.setEnabled(true); // odbezpiecz nowa gra
        
        // kto wygral badz zremisowal - wyjscie dla konsoli
        String[] gr = new String[2];
        if (ktoryGracz==0) { gr[0] = "BIAŁY"; gr[1] = "CZARNY";}
        else               { gr[0] = "CZARNY"; gr[1] = "BIAŁY";}
        
        String wyn = "";
        if (koniecGry == 1 )     { wyn = gr[0] + " król - MAT. Wygrywa " + gr[1] + " gracz."; }
        else if (koniecGry == 2) { wyn = gr[0] + " król - PAT. REMIS."; }
        else if (koniecGry == 3) { wyn = "Trzykrotne powtórzenie pozycji. REMIS."; }
        else if (koniecGry == 4) { wyn = "50 ruchów - brak bicia lub ruchu pionem. REMIS."; }
        konsola.setText(konsola.getText() + wyn + "\n");
        //statystyka
        if (ktoGraBialyGracz == 0)
        {
            konsola.setText(konsola.getText() + "********** STATYSTYKA BIAŁY GRACZ **********\n");
            long sumaLisci=0;
            int licznikRuchow=0;
            int rozmiar = trybPrzegladania.liczbaOcenionychLisci.size();
            for (int i=0;i<rozmiar;i=i+2)
            {
                sumaLisci = sumaLisci + trybPrzegladania.liczbaOcenionychLisci.get(i);
                licznikRuchow++;
            }
            konsola.setText(konsola.getText() + "W " + licznikRuchow + " RUCHACH OCENIONO " + sumaLisci +" LIŚCI.\n");
            konsola.setText(konsola.getText() + "ŚREDNIO " + (sumaLisci/licznikRuchow) + " LIŚCI NA RUCH.\n");
        }
        if (ktoGraCzarnyGracz == 0)
        {
            konsola.setText(konsola.getText() + "********** STATYSTYKA CZARNY GRACZ **********\n");
            long sumaLisci=0;
            int licznikRuchow=0;
            int rozmiar = trybPrzegladania.liczbaOcenionychLisci.size();
            for (int i=1;i<rozmiar;i=i+2)
            {
                sumaLisci = sumaLisci + trybPrzegladania.liczbaOcenionychLisci.get(i);
                licznikRuchow++;
            }
            konsola.setText(konsola.getText() + "W " + licznikRuchow + " RUCHACH OCENIONO " + sumaLisci +" LIŚCI.\n");
            konsola.setText(konsola.getText() + "ŚREDNIO " + (sumaLisci/licznikRuchow) + " LIŚCI NA RUCH.\n");
        }
        
        
    }
    
    private void sterowanieGra()
    {
        // najpierw sprawdzamy kto teraz wykonuje ruch
        
////////////////////////////////////////////////BIALY GRACZ//////////////////////////////////////////////////////////////    
        if (szachownica.zwrocGracza()==0)
        {
            //wlaczamy i wylaczamy zegary
            zegarBialegoGracza.wlacz();
            zegarCzarnegoGracza.wylacz();
            
            if (ktoGraBialyGracz==1) //czyli ruch wykonuje czlowiek
            {
                jMenuItem3.setEnabled(true);
                jMenuItem8.setEnabled(true); // dostep do Sztucznej Inteligencji
                jMenuItem1.setEnabled(true); // odbezpiecz nowa gra
                //sprawdzamy czy ten gracz nie ma szacha, badz mata badz pata
                // czyli warunki gry
                
                Wezel spisRuchow = new Wezel(new Posuniecie(0,0,0,0));
                int liczbaMozliwychPosuniec = spisRuchow.generujDzieci(szachownica,false);
                
                int koniecGry = sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,false);
                
                if (koniecGry==0)
                {
                    String szach = "";
                    if (spisRuchow.szachKrola==true)
                    {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(this,"Biały król zagrożony biciem!","SZACH",JOptionPane.WARNING_MESSAGE);
                        szach = ", SZACH króla.";
                    }
                    konsola.setText(konsola.getText() + "BG LMP - " + liczbaMozliwychPosuniec + szach + "\n");
                    // dajemy graczowi dostep do wygenerowania ruchu
                    blokadaMyszy=false;
                    
                }
                
                else  //koniec gry 
                {
                    koniecGry(0,koniecGry); 
                    sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,true);
                }
            konsola.setCaretPosition(konsola.getDocument().getLength());
            }    
            
 /////////////////////////////////////BIALY KOMPUTER///////////////////////////////////////////////////////////////           
            
            else
            {
                jMenuItem3.setEnabled(false);
                jMenuItem8.setEnabled(false); // dostep do Sztucznej Inteligencji
                jMenuItem1.setEnabled(false); // odbezpiecz nowa gra
                Wezel spisRuchow = new Wezel(new Posuniecie(0,0,0,0));
                int liczbaMozliwychPosuniec = spisRuchow.generujDzieci(szachownica,false);
                int koniecGry = sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,false);
                
                if (koniecGry==0)
                {
                    String szach = "";
                    if (spisRuchow.szachKrola==true) { szach = ", SZACH króla."; }
                    konsola.setText(konsola.getText() + "BG LMP - " + liczbaMozliwychPosuniec + szach + "\n");
                    GeneratorPosuniecSI SI = new GeneratorPosuniecSI();
                    SI.setPriority(3);
                    SI.start();
                }
                
                else  //koniec gry 
                {
                    koniecGry(0,koniecGry);
                    sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,true);
                }
            konsola.setCaretPosition(konsola.getDocument().getLength());
            }
            
        }
         
///////////////////////////////CZARNY GRACZ////////////////////////////////////////////////////////////////    
        
        else
        {
            //wlaczamy i wylaczamy zegary
            zegarBialegoGracza.wylacz();
            zegarCzarnegoGracza.wlacz();
            
            if (ktoGraCzarnyGracz==1)
            {
                jMenuItem3.setEnabled(true);
                jMenuItem8.setEnabled(true); // dostep do Sztucznej Inteligencji
                jMenuItem1.setEnabled(true); // odbezpiecz nowa gra
                Wezel spisRuchow = new Wezel(new Posuniecie(0,0,0,0));
                int liczbaMozliwychPosuniec = spisRuchow.generujDzieci(szachownica,false);
                
                int koniecGry = sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,false);
                
                if (koniecGry==0)
                {
                    String szach = "";
                    if (spisRuchow.szachKrola==true)
                    {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(this,"Czarny król zagrożony biciem!","SZACH",JOptionPane.WARNING_MESSAGE);
                        szach = ", SZACH króla.";
                    }
                    konsola.setText(konsola.getText() + "CG LMP - " + liczbaMozliwychPosuniec + szach + "\n");
                    // dajemy graczowi dostep do wygenerowania ruchu
                    blokadaMyszy=false;
                }
                
                else  //koniec gry 
                {
                    koniecGry(1,koniecGry);  
                    sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,true);
                }
            konsola.setCaretPosition(konsola.getDocument().getLength());
            }
            
///////////////////////////////////////CZARNY KOMPUTER///////////////////////////////////////////////////////////          
            
            else 
            {
                jMenuItem3.setEnabled(false);
                jMenuItem8.setEnabled(false); // dostep do Sztucznej Inteligencji
                jMenuItem1.setEnabled(false); // odbezpiecz nowa gra
                Wezel spisRuchow = new Wezel(new Posuniecie(0,0,0,0));
                int liczbaMozliwychPosuniec = spisRuchow.generujDzieci(szachownica,false);
                int koniecGry = sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,false);
                
                if (koniecGry==0)
                {
                    String szach = "";
                    if (spisRuchow.szachKrola==true) { szach = ", SZACH króla."; }
                    konsola.setText(konsola.getText() + "CG LMP - " + liczbaMozliwychPosuniec + szach + "\n");
                    GeneratorPosuniecSI SI = new GeneratorPosuniecSI();
                    SI.setPriority(3);
                    SI.start();
                }
                
                else  //koniec gry 
                {
                    koniecGry(1,koniecGry);
                    sprawdzWarunkiKoncoweGry(szachownica,spisRuchow,true);
                }
            konsola.setCaretPosition(konsola.getDocument().getLength());
            }
            
        }
 
    }
    
   
/////////////////////////////////////////////////////////////////////////////////////////////////////////    
    
    private boolean obsluzIWykonajPosuniecieCzlowiek(Posuniecie ruch)
    {
        boolean czyWykonanoRuch=false;
        //otrzymalismy ruch
        //najpierw nalezy na podstawie szachownicy wygenerowac wszystkie mozliwe ruchy 
        // wykorzystamy do tego wezel
        Wezel spisRuchow = new Wezel(new Posuniecie(0,0,0,0));
        // generujemy dzieci wezla
        spisRuchow.generujDzieci(szachownica,false);
        // teraz wystarczy sprawdzic czy taki ruch jest zawarty w drzewie
        for (int i=0;i<spisRuchow.dzieci.size();i++)
        {
            Posuniecie pomoc = spisRuchow.dzieci.get(i).poprzedniRuchPrzeciwnika;
            
            if ( (pomoc.pozycjaPoczatkowa.x==ruch.pozycjaPoczatkowa.x)&&
                 (pomoc.pozycjaPoczatkowa.y==ruch.pozycjaPoczatkowa.y)&&
                 (pomoc.pozycjaKoncowa.x==ruch.pozycjaKoncowa.x)&&
                 (pomoc.pozycjaKoncowa.y==ruch.pozycjaKoncowa.y) )
            {
                String gr = "BG"; 
                if (szachownica.zwrocGracza()==1) { gr = "CG";} 
                konsola.setText(konsola.getText() + (String.valueOf(trybPrzegladania.numerRuchu+1)) +". " + 
                                 gr + "   " +przeksztalcWspolrzedneDoStringa(pomoc) + "\n");
                
                czyWykonanoRuch=true;
                // czyli moge taki ruch wykonac - wszystko sprawdzone,
                int wynik = szachownica.wykonajRuch(ruch);
                // dodanie ruchu do trybu przegladania
                if (wynik != 3) { trybPrzegladania.dodajRuch(ruch,0,0); }
                
                if (wynik == 3) //obsluzenie promocji
                {
                   Toolkit.getDefaultToolkit().beep();
                   Object opcje[] ={"HETMAN","WIEŻA","SKOCZEK","GONIEC"};
                   int odpowiedz = JOptionPane.showOptionDialog(this,"Na jaką figurę chcesz zamienić piona?","PROMOCJA",
                                   JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE, null,opcje,opcje[0]);
                   //-1 - x , 0 - hetman, 1- wieza, 2 - skoczek, 3 - goniec
                   
                   // dodanie ruchu do trybu przegladania z dopiskiem typu promocji
                   trybPrzegladania.dodajRuch(ruch,odpowiedz,0);
                   
                   szachownica.promocjaPodmienFigure(ruch.pozycjaKoncowa,odpowiedz);
                }
                jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu)); //uaktualnienie numeru ruchu    
                break; //nie ma po co dlaje wykonywac tej petli
                //ponadto w przypadku promocji for nie wywola sie 4 razy
            }
        }
        return czyWykonanoRuch;
    }
        
    private void generatorRuchowCzlowiek(Point p,int flaga)
    {
        if (flaga==0) //nacisnieto przycisk myszki
        {
            wspolrzedneMyszy=p;
            zaznaczonePolePoczatkowe = zamienWspolrzedneNaNumerPola(p);
            jPanel1.repaint();
        }
        if (flaga==1) //przeciaganie kursorem
        {
            wspolrzedneMyszy=p;
            jPanel1.repaint();
        }
        if (flaga==2) // upuszczanie 
        {
            zaznaczonePoleKoncowe = zamienWspolrzedneNaNumerPola(p);
            
            if ( (zaznaczonePolePoczatkowe.x!=(-1)) && (zaznaczonePolePoczatkowe.y!=(-1)) && 
                 (zaznaczonePoleKoncowe.x!=(-1)) && (zaznaczonePoleKoncowe.y!=(-1)) )
                  
            {
                if (szachownica.plansza[zaznaczonePolePoczatkowe.y][zaznaczonePolePoczatkowe.x]!=null)
                // warunek wyeliminuje przesuniecia z pustego pola
                {
                    blokadaMyszy=true; //Blokada Myszki
                    Posuniecie ruch = new Posuniecie(zaznaczonePolePoczatkowe.x,zaznaczonePolePoczatkowe.y,
                                                     zaznaczonePoleKoncowe.x,zaznaczonePoleKoncowe.y);
                
                    boolean czyWykonanoRuch = obsluzIWykonajPosuniecieCzlowiek(ruch);
                    wspolrzedneMyszy.x=0;
                    wspolrzedneMyszy.y=0;
                    zaznaczonePolePoczatkowe.x=-1;
                    zaznaczonePolePoczatkowe.y=-1;
                    jPanel1.repaint();
                    if (czyWykonanoRuch ==true)
                    {                       
                        sterowanieGra();
                    }
                    else
                    {
                        blokadaMyszy=false;
                    }
                }
            }
            else
            {
                wspolrzedneMyszy.x=0;
                wspolrzedneMyszy.y=0;
                zaznaczonePolePoczatkowe.x=-1;
                zaznaczonePolePoczatkowe.y=-1;
                jPanel1.repaint(); 
            }
           
        }
    }
    
    private void testujSzachownice(Szachownica sz, Szachownica kopia)
    {
        
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                int warunek=0;
                if (sz.plansza[i][j]!= null)
                {
                    if ((sz.plansza[i][j] instanceof Krol) && (kopia.plansza[i][j] instanceof Krol) )
                    {
                        Krol f = (Krol)sz.plansza[i][j];
                        Krol fk = (Krol)kopia.plansza[i][j];
                        if ( (f.czyJuzSieRuszal == fk.czyJuzSieRuszal) && (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    }
                    else if ((sz.plansza[i][j] instanceof Pion) && (kopia.plansza[i][j] instanceof Pion) )
                    {
                        Pion f = (Pion)sz.plansza[i][j];
                        Pion fk = (Pion)kopia.plansza[i][j];
                        if ( (f.czyJuzSieRuszal == fk.czyJuzSieRuszal) && (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    }
                    else if ((sz.plansza[i][j] instanceof Wieza) && (kopia.plansza[i][j] instanceof Wieza) )
                    {
                        Wieza f = (Wieza)sz.plansza[i][j];
                        Wieza fk = (Wieza)kopia.plansza[i][j];
                        if ( (f.czyJuzSieRuszal == fk.czyJuzSieRuszal) && (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    }
                    
                    
                    else if ((sz.plansza[i][j] instanceof Hetman) && (kopia.plansza[i][j] instanceof Hetman) )
                    {
                        Hetman f = (Hetman)sz.plansza[i][j];
                        Hetman fk = (Hetman)kopia.plansza[i][j];
                        if ( (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    }
                    else if ((sz.plansza[i][j] instanceof Skoczek) && (kopia.plansza[i][j] instanceof Skoczek) )
                    {
                        Skoczek f = (Skoczek)sz.plansza[i][j];
                        Skoczek fk = (Skoczek)kopia.plansza[i][j];
                        if ( (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    }    
                    else if ((sz.plansza[i][j] instanceof Goniec) && (kopia.plansza[i][j] instanceof Goniec) )
                    {
                        Goniec f = (Goniec)sz.plansza[i][j];
                        Goniec fk = (Goniec)kopia.plansza[i][j];
                        if ( (f.kolorFigury == fk.kolorFigury) )
                        {
                            warunek=1;
                        }
                    } 
                }
                else
                {
                    if (kopia.plansza[i][j]== null) // sz ma null, sprzwdzamy czy na kopii tez jest
                    {
                        warunek=1;
                    }
                }
                if (warunek == 0)
                {
                    System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC POL NA PLANSZY!");
                }
            }
        }
        if (sz.gracz!=kopia.gracz)
        {
            System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC GRACZY!");
        }
        if (sz.spisPoprzednichRuchow.size() != kopia.spisPoprzednichRuchow.size())
        {
            System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC ILOSCI SPIS POPRZEDNICH RUCHOW!");
        }
        if ( (sz.pozycjaBialegoKrola.x != kopia.pozycjaBialegoKrola.x) ||
             (sz.pozycjaBialegoKrola.y != kopia.pozycjaBialegoKrola.y)   )
        {
            System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC POZYCJA BIALEGO KROLA!");
        }
        if ( (sz.pozycjaCzarnegoKrola.x != kopia.pozycjaCzarnegoKrola.x) ||
             (sz.pozycjaCzarnegoKrola.y != kopia.pozycjaCzarnegoKrola.y)   )
        {
            System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC POZYCJA CZARNEGO KROLA!");
        }
        if (sz.zapis50Posuniec != kopia.zapis50Posuniec)
        {
            System.out.println("TESTUJ SZACHOWNICE - BLAD - NIEZGODNOSC ZAPIS 50 POSUNIEC!");
        }
        
        
    }
    
    
    private void wczytajObrazy()
    {
        // 0 - szachownica
        // 1 - b pion, 2 - b wieza, 3 - b skoczek, 4 - b goniec, 5 - b hetman, 6 - b krol
        // 7 - c pion, 8 - c wieza, 9 - c skoczek, 10 - c goniec, 11 - c hetman, 12 - c krol
        
        nazwyGrafik[0]="szachownica.png"; 
        nazwyGrafik[1]="bp.png"; nazwyGrafik[2]="bw.png"; nazwyGrafik[3]="bs.png"; nazwyGrafik[4]="bg.png";
        nazwyGrafik[5]="bh.png"; nazwyGrafik[6]="bk.png"; nazwyGrafik[7]="cp.png"; nazwyGrafik[8]="cw.png";
        nazwyGrafik[9]="cs.png"; nazwyGrafik[10]="cg.png"; nazwyGrafik[11]="ch.png"; nazwyGrafik[12]="ck.png";
        
        obrazy[0] = new BufferedImage(rozmiarSzachownicy,rozmiarSzachownicy, BufferedImage.TYPE_INT_RGB);
        for (int i=1;i<13;i++)
        {
            obrazy[i] = new BufferedImage(rozmiarPolaNaSzachownicy,rozmiarPolaNaSzachownicy,BufferedImage.TYPE_INT_RGB);
        }
        
        for (int a=0;a<13;a++)
        {
            URL url = this.getClass().getClassLoader().getResource(nazwyGrafik[a]);
            try { obrazy[a] = ImageIO.read(url); }
            catch (IOException ex) 
            { 
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,"Wystąpił problem z wczytaniem obrazów z jar.\nZmień 'ścieżkę dostępu/katalog' programu.","BŁĄD",JOptionPane.ERROR_MESSAGE);
                System.exit(0); 
            }
        }
    }
    
    private String przeksztalcWspolrzedneDoStringa(Posuniecie pos)
    {
        String[] litery = {"A","B","C","D","E","F","G","H"};
        String[] cyfry  = {"1","2","3","4","5","6","7","8"};
        String wynik = "";
        
        wynik = litery[pos.pozycjaPoczatkowa.x] + cyfry[7-pos.pozycjaPoczatkowa.y] + " - " +
                litery[pos.pozycjaKoncowa.x] + cyfry[7-pos.pozycjaKoncowa.y];
        return wynik;
    }
    
    private Point zamienWspolrzedneNaNumerPola(Point p)
    {
        boolean ramkaX=true;
        Point pozycja = new Point(0,0);
        for(int i=0;i<8;i++)
        {
            if (i*rozmiarPolaNaSzachownicy+ramkaSzachownicy<=(p.x)
            &&((i+1)*rozmiarPolaNaSzachownicy+ramkaSzachownicy>=(p.x)))
            {
                pozycja.x=i;
                ramkaX=false;
                break;
            }
        }
        if (ramkaX==true) {pozycja.x=-1;}
        
        boolean ramkaY=true;
        for(int i=0;i<8;i++)
        {
            if (i*rozmiarPolaNaSzachownicy+ramkaSzachownicy<=(p.y)
            &&((i+1)*rozmiarPolaNaSzachownicy+ramkaSzachownicy>=(p.y)))
            {
                pozycja.y=i;
                ramkaY=false;
                break;
            }
        }
        if (ramkaY==true) {pozycja.y=-1;}
        
    return pozycja;
    }
 
    
    
    
//////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        bialyRB1 = new javax.swing.JRadioButton();
        bialyRB2 = new javax.swing.JRadioButton();
        bialyGlebokosc = new javax.swing.JSlider();
        bialyCSort = new javax.swing.JCheckBox();
        bialySort = new javax.swing.JSlider();
        jPanel8 = new javax.swing.JPanel();
        bialyOcenaMaterialna = new javax.swing.JCheckBox();
        bialyOcenaPozycyjna = new javax.swing.JCheckBox();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        czarnyRB1 = new javax.swing.JRadioButton();
        czarnyRB2 = new javax.swing.JRadioButton();
        czarnyGlebokosc = new javax.swing.JSlider();
        czarnyCSort = new javax.swing.JCheckBox();
        czarnySort = new javax.swing.JSlider();
        jPanel11 = new javax.swing.JPanel();
        czarnyOcenaMaterialna = new javax.swing.JCheckBox();
        czarnyOcenaPozycyjna = new javax.swing.JCheckBox();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new Wyswietlacz(rozmiarSzachownicy);
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        konsola = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        jDialog1.setTitle("Ustawienia Sztucznej Inteligencji");
        jDialog1.setMinimumSize(new java.awt.Dimension(450, 475));
        jDialog1.setModal(true);
        jDialog1.setResizable(false);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Biały Gracz", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Generowanie Drzewa"));

        buttonGroup1.add(bialyRB1);
        bialyRB1.setSelected(true);
        bialyRB1.setText("Min - Max");

        buttonGroup1.add(bialyRB2);
        bialyRB2.setText("Alpha - Beta");

        bialyGlebokosc.setMajorTickSpacing(1);
        bialyGlebokosc.setMaximum(8);
        bialyGlebokosc.setMinimum(1);
        bialyGlebokosc.setPaintLabels(true);
        bialyGlebokosc.setPaintTicks(true);
        bialyGlebokosc.setSnapToTicks(true);
        bialyGlebokosc.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Głębokość drzewa:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N

        bialyCSort.setText("A - B sortuj węzły");
        bialyCSort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                bialyCSortItemStateChanged(evt);
            }
        });

        bialySort.setMajorTickSpacing(1);
        bialySort.setMaximum(8);
        bialySort.setMinimum(1);
        bialySort.setPaintLabels(true);
        bialySort.setPaintTicks(true);
        bialySort.setSnapToTicks(true);
        bialySort.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Głębokość sortowania:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bialyCSort)
                    .addComponent(bialyRB1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bialyRB2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bialyGlebokosc, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                    .addComponent(bialySort, 0, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bialyRB1)
                .addGap(3, 3, 3)
                .addComponent(bialyRB2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bialyGlebokosc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bialyCSort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bialySort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(4, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Funkcja Oceny"));

        bialyOcenaMaterialna.setSelected(true);
        bialyOcenaMaterialna.setText("Ocena materialna");
        bialyOcenaMaterialna.setEnabled(false);

        bialyOcenaPozycyjna.setText("Ocena pozycyjna");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bialyOcenaMaterialna)
                    .addComponent(bialyOcenaPozycyjna))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(bialyOcenaMaterialna)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bialyOcenaPozycyjna)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton4.setText("Zapisz");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Anuluj");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "Czarny Gracz", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Generowanie Drzewa"));

        buttonGroup2.add(czarnyRB1);
        czarnyRB1.setSelected(true);
        czarnyRB1.setText("Min - Max");

        buttonGroup2.add(czarnyRB2);
        czarnyRB2.setText("Alpha - Beta");

        czarnyGlebokosc.setMajorTickSpacing(1);
        czarnyGlebokosc.setMaximum(8);
        czarnyGlebokosc.setMinimum(1);
        czarnyGlebokosc.setPaintLabels(true);
        czarnyGlebokosc.setPaintTicks(true);
        czarnyGlebokosc.setSnapToTicks(true);
        czarnyGlebokosc.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Głębokość drzewa:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N

        czarnyCSort.setText("A - B sortuj węzły");
        czarnyCSort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                czarnyCSortItemStateChanged(evt);
            }
        });

        czarnySort.setMajorTickSpacing(1);
        czarnySort.setMaximum(8);
        czarnySort.setMinimum(1);
        czarnySort.setPaintLabels(true);
        czarnySort.setPaintTicks(true);
        czarnySort.setSnapToTicks(true);
        czarnySort.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Głębokość sortowania:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(czarnyRB1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(czarnyRB2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(czarnyGlebokosc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                        .addComponent(czarnyCSort, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(czarnySort, 0, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(czarnyRB1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(czarnyRB2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(czarnyGlebokosc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(czarnyCSort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(czarnySort, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(4, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Funkcja Oceny"));

        czarnyOcenaMaterialna.setSelected(true);
        czarnyOcenaMaterialna.setText("Ocena materialna");
        czarnyOcenaMaterialna.setEnabled(false);

        czarnyOcenaPozycyjna.setText("Ocena pozycyjna");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(czarnyOcenaMaterialna)
                    .addComponent(czarnyOcenaPozycyjna))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(czarnyOcenaMaterialna)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(czarnyOcenaPozycyjna)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(72, 72, 72))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(16, 16, 16))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "CZAS GRY", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "BIAŁY GRACZ"));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel1.setText("00:00:00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "CZARNY GRACZ"));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel2.setText("00:00:00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "KONSOLA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        konsola.setColumns(20);
        konsola.setEditable(false);
        konsola.setRows(5);
        jScrollPane1.setViewportView(konsola);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setText("WSTECZ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("DALEJ");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18));
        jLabel3.setText("0");

        jProgressBar1.setStringPainted(true);

        jMenu1.setText("Plik");

        jMenuItem1.setText("Nowa Gra");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Zamknij");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edycja");

        jMenuItem3.setText("Cofnij ruch");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);
        jMenu2.add(jSeparator1);

        jMenuItem4.setText("Przerwij obliczenia");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Ponów obliczenia");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Ustawienia");

        jMenuItem8.setText("Sztuczna Inteligencja");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuBar1.add(jMenu3);

        jMenu5.setText("Pomoc");

        jMenuItem6.setText("O programie");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem6);

        jMenuItem7.setText("Zasady gry");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem7);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(92, 92, 92)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton3)
                            .addComponent(jLabel3)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        // TODO add your handling code here:
       if (blokadaMyszy==false)
       {
        generatorRuchowCzlowiek(new Point(evt.getX(),evt.getY()),0);
       }
       
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        // TODO add your handling code here:
        if (blokadaMyszy==false)
        {
            generatorRuchowCzlowiek(new Point(evt.getX(),evt.getY()),1);
        }
        
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:
        if (blokadaMyszy==false)
        {
            generatorRuchowCzlowiek(new Point(evt.getX(),evt.getY()),2);
        }
    }//GEN-LAST:event_jPanel1MouseReleased

//nowa gra
private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    nowaGra();
}//GEN-LAST:event_jMenuItem1ActionPerformed

//zamknij
private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem2ActionPerformed

//o programie
private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
Toolkit.getDefaultToolkit().beep();
JOptionPane.showMessageDialog(this,"SZACHY\nKrzysztof Hłobaż\nIkony figur pobrano z http://pl.wikipedia.org","O PROGRAMIE",JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jMenuItem6ActionPerformed

//zasady gry
private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
Toolkit.getDefaultToolkit().beep();
JOptionPane.showMessageDialog(this,"Zasady gry w szachy opisane są na http://pl.wikipedia.org/wiki/Zasady_gry_w_szachy","ZASADY GRY",JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jMenuItem7ActionPerformed

//cofnij ruch
private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    if (szachownica.spisPoprzednichRuchow.size()>0)
    {
        if (ktoGraBialyGracz==0 || ktoGraCzarnyGracz==0) // jakis komputer gra
        {
            if (szachownica.spisPoprzednichRuchow.size()>=2)
            {
                szachownica.cofnijRuch();
                szachownica.cofnijRuch();
                trybPrzegladania.usunRuch();
                trybPrzegladania.usunRuch();
                SIBialy.wyzerujMatRegulatorGlebokosciDrzewa();
                SICzarny.wyzerujMatRegulatorGlebokosciDrzewa();
                // cofamy o 2 ruchy
                konsola.setText(konsola.getText() + "Cofnięto o 2 posunięcia!\n");
                
            }
        }
        else //tylko 2 gracze
        {
            szachownica.cofnijRuch();
            trybPrzegladania.usunRuch();
            SIBialy.wyzerujMatRegulatorGlebokosciDrzewa();
            SICzarny.wyzerujMatRegulatorGlebokosciDrzewa();
            konsola.setText(konsola.getText() + "Cofnięto o 1 posunięcie!\n");
        }
        //nalezy ustawic odpowiednio timery
        if (szachownica.zwrocGracza()==0) { zegarBialegoGracza.wlacz(); zegarCzarnegoGracza.wylacz(); }
        else                              { zegarBialegoGracza.wylacz(); zegarCzarnegoGracza.wlacz(); }
        // uaktualnienie nrumeru ruchow
        jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu));
        jPanel1.repaint();
    }
}//GEN-LAST:event_jMenuItem3ActionPerformed
//sztuczna inteligencja
private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
    int nrAlgorytmu = SIBialy.zwrocNrAlgorytmu();
    if (nrAlgorytmu == 1) {bialyRB1.setSelected(true);}
    else if (nrAlgorytmu == 2) {bialyRB2.setSelected(true);}
    int glebokosc = SIBialy.zwrocGlebokosc();
    bialyGlebokosc.setValue(glebokosc);
    boolean czySortowac = SIBialy.zwrocCzySortowac();
    bialyCSort.setSelected(czySortowac);
    int glebokoscSortowania = SIBialy.zwrocGlebokoscSortowania();
    bialySort.setValue(glebokoscSortowania);
    bialySort.setEnabled(czySortowac);
    //fcja oceny
    boolean ocenaPozycyjna = SIBialy.zwrocFunkcjeOceny().zwrocCzyOceniacPozycyjnie();
    bialyOcenaPozycyjna.setSelected(ocenaPozycyjna);
    
    
    nrAlgorytmu = SICzarny.zwrocNrAlgorytmu();
    if (nrAlgorytmu == 1) {czarnyRB1.setSelected(true);}
    else if (nrAlgorytmu == 2) {czarnyRB2.setSelected(true);}
    glebokosc = SICzarny.zwrocGlebokosc();
    czarnyGlebokosc.setValue(glebokosc);
    czySortowac = SICzarny.zwrocCzySortowac();
    czarnyCSort.setSelected(czySortowac);
    glebokoscSortowania = SICzarny.zwrocGlebokoscSortowania();
    czarnySort.setValue(glebokoscSortowania);
    czarnySort.setEnabled(czySortowac);
    //fcja oceny
    ocenaPozycyjna = SICzarny.zwrocFunkcjeOceny().zwrocCzyOceniacPozycyjnie();
    czarnyOcenaPozycyjna.setSelected(ocenaPozycyjna);
    
    jDialog1.setLocationRelativeTo(this);
    jDialog1.setVisible(true);
}//GEN-LAST:event_jMenuItem8ActionPerformed

//sztuczna inteligencja - anuluj
private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    jDialog1.setVisible(false);
}//GEN-LAST:event_jButton5ActionPerformed

//sztuczna inteligencja - zapisz
private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    //bialy gracz
    int nrAlgorytmu=1; 
    if (bialyRB1.isSelected()==true) {nrAlgorytmu = 1;}
    else if(bialyRB2.isSelected()==true) {nrAlgorytmu = 2;}
    int glebokosc = bialyGlebokosc.getValue();
    boolean czySortowac = bialyCSort.isSelected();
    int glebokoscSortowania = bialySort.getValue();
    SIBialy.zmienUstawienia(glebokosc,nrAlgorytmu,czySortowac,glebokoscSortowania);  
    //fcja oceny
    boolean czyOceniacPozycyjnie = bialyOcenaPozycyjna.isSelected();
    SIBialy.zwrocFunkcjeOceny().zmienUstawienia(czyOceniacPozycyjnie);
    
        
    //czarny gracz
    if (czarnyRB1.isSelected()==true) {nrAlgorytmu = 1;}
    else if(czarnyRB2.isSelected()==true) {nrAlgorytmu = 2;}
    glebokosc = czarnyGlebokosc.getValue();
    czySortowac = czarnyCSort.isSelected();
    glebokoscSortowania = czarnySort.getValue();
    SICzarny.zmienUstawienia(glebokosc,nrAlgorytmu,czySortowac,glebokoscSortowania);  
    //fcja oceny
    czyOceniacPozycyjnie = czarnyOcenaPozycyjna.isSelected();
    SICzarny.zwrocFunkcjeOceny().zmienUstawienia(czyOceniacPozycyjnie);
    
    
    SIBialy.wyzerujMatRegulatorGlebokosciDrzewa();
    SICzarny.wyzerujMatRegulatorGlebokosciDrzewa();
    jDialog1.setVisible(false);
}//GEN-LAST:event_jButton4ActionPerformed
// przycisk wstecz
private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    trybPrzegladania.numerRuchu--;
    szachownica.cofnijRuch();
    
    if (trybPrzegladania.numerRuchu == 0)
    {
        jButton1.setEnabled(false);
    }
    jButton3.setEnabled(true);
    jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu));
    jPanel1.repaint();
}//GEN-LAST:event_jButton1ActionPerformed

// przycisk dalej
private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    trybPrzegladania.numerRuchu++;
    Posuniecie ref = trybPrzegladania.posuniecia.get(trybPrzegladania.numerRuchu-1);
    
    int wynik = szachownica.wykonajRuch(ref);
    if (wynik ==3) //promocja
    {
        szachownica.promocjaPodmienFigure(ref.pozycjaKoncowa,trybPrzegladania.promocja.get(trybPrzegladania.numerRuchu-1));
    }
    
    if (trybPrzegladania.numerRuchu == trybPrzegladania.posuniecia.size())
    {
        jButton3.setEnabled(false);
    }
    jButton1.setEnabled(true);
    jLabel3.setText(String.valueOf(trybPrzegladania.numerRuchu));
    jPanel1.repaint();
}//GEN-LAST:event_jButton3ActionPerformed

//przerwij gre
private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
    SIBialy.ustawFlageZakonczenia(true);
    SICzarny.ustawFlageZakonczenia(true);
    zegarBialegoGracza.wylacz();
    zegarCzarnegoGracza.wylacz();
    jMenuItem4.setEnabled(false); //przerwij obliczenia
    konsola.setText(konsola.getText() + "Czekaj...\n");
    konsola.setCaretPosition(konsola.getDocument().getLength());
    
}//GEN-LAST:event_jMenuItem4ActionPerformed
// wznow gre
private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
    SIBialy.ustawFlageZakonczenia(false);
    SICzarny.ustawFlageZakonczenia(false);
    jMenuItem5.setEnabled(false); //wznow obliczenia
    sterowanieGra();
}//GEN-LAST:event_jMenuItem5ActionPerformed

// kliknieto na a-b sortuj wezly bialego gracza
private void bialyCSortItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_bialyCSortItemStateChanged
    if (bialyCSort.isSelected()==true) { bialySort.setEnabled(true); }
    else                               { bialySort.setEnabled(false); }
    
}//GEN-LAST:event_bialyCSortItemStateChanged

private void czarnyCSortItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_czarnyCSortItemStateChanged
    if (czarnyCSort.isSelected()==true) { czarnySort.setEnabled(true); }
    else                               { czarnySort.setEnabled(false); }
}//GEN-LAST:event_czarnyCSortItemStateChanged
 
/////////////////////////////////////MAIN////////////////////////////////////////////////////////////////    


    /**
     * @param args the command line arguments
     */
       
        public static void main(String[] args)
        {
            // TODO code application logic here
            java.awt.EventQueue.invokeLater(new Runnable() 
            {
                public void run() 
                {
                    new WyswietlanieISterowanie().setVisible(true);
                }
            });
        }
 
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox bialyCSort;
    private javax.swing.JSlider bialyGlebokosc;
    private javax.swing.JCheckBox bialyOcenaMaterialna;
    private javax.swing.JCheckBox bialyOcenaPozycyjna;
    private javax.swing.JRadioButton bialyRB1;
    private javax.swing.JRadioButton bialyRB2;
    private javax.swing.JSlider bialySort;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox czarnyCSort;
    private javax.swing.JSlider czarnyGlebokosc;
    private javax.swing.JCheckBox czarnyOcenaMaterialna;
    private javax.swing.JCheckBox czarnyOcenaPozycyjna;
    private javax.swing.JRadioButton czarnyRB1;
    private javax.swing.JRadioButton czarnyRB2;
    private javax.swing.JSlider czarnySort;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea konsola;
    // End of variables declaration//GEN-END:variables
}




