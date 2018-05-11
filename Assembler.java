import java.util.*;
import java.io.*;
class Assembler
{
	String [][]mot,st,lt;
	String pot[];
	String bt[][],s,inputfile;
	BufferedReader inp,p2inp;
	BufferedWriter output,p2op;
	FileWriter fw1,fw2;
	File op;
	int stc=0,ltc=0,lc=0,prelc=0,btc=0;
	Assembler(String inputfile)
	{
		this.inputfile=inputfile;
		mot=new String[10][4];
		st=new String[15][5];
		lt=new String[10][5];
		pot=new String[8];
		bt=new String[6][2];
		
		mot[0][0]="LA";
		mot[1][0]="L";
		mot[2][0]="A";
		mot[3][0]="C";
		mot[4][0]="BNE";
		mot[5][0]="ST";
		mot[6][0]="SR";
		mot[7][0]="AR";
		mot[8][0]="LR";
		mot[9][0]="BR";
		
		for(int i=0;i<6;i++)
		{
			mot[i][1]="RX";
			mot[i][3]="4";
		}
		for(int i=6;i<10;i++)
		{
			mot[i][1]="RR";
			mot[i][3]="2";
		}

		mot[0][2]="01";
		mot[1][2]="03";
		mot[2][2]="05";
		mot[3][2]="06";
		mot[4][2]="07";
		mot[5][2]="09";
		mot[6][2]="02";
		mot[7][2]="04";
		mot[8][2]="08";
		mot[9][2]="15";
		
		pot[0]="START";
		pot[1]="EQU";
		pot[2]="DC";
		pot[3]="DS";
		pot[4]="USING";
		pot[5]="END";
		pot[6]="DROP";
		pot[7]="LTORG";

	}

	void pass1() throws IOException
	{
		int i;
		
		try
		{
			inp=new BufferedReader(new FileReader(inputfile));
			op=new File("asspass1_output.txt");
			if(!op.exists())
				op.createNewFile();
			fw1=new FileWriter(op.getAbsoluteFile());
			output=new BufferedWriter(fw1);
			
			while((s=inp.readLine())!=null)
			{
				StringTokenizer stn=new StringTokenizer(s);
				String str[]=new String[stn.countTokens()];

				for(i=0;i<str.length;i++)
				{
					str[i]=stn.nextToken();
				}
				for(i=0;i<pot.length;i++)
					if(str[0].equalsIgnoreCase(pot[i]))
						break;
				if(i!=pot.length)
					searchPot1(str);
				else
				{
					for(i=0;i<mot.length;i++)
						if(str[0].equalsIgnoreCase(mot[i][0]))
							break;

					if(i!=mot.length)
					{
						searchMot1(str,i);	
					}
					else
					{
						for(i=0;i<pot.length;i++)
							if(str[1].equalsIgnoreCase(pot[i]))
								break;
						if(i!=pot.length)
							searchPot1(str);

						else 
						{
							for(i=0;i<mot.length;i++)
								if(str[1].equalsIgnoreCase(mot[i][0]))
									break;

							if(i!=mot.length)
							{
								st[stc][0]=str[0];
								st[stc][1]=lc+"";
								st[stc][2]="0";
								st[stc][3]="4";
								st[stc++][4]="R";
								String string[]=new String[str.length-1];
								for(int j=0;j<string.length;j++)
									string[j]=str[j+1];
								searchMot1(string,i);
							}
							else
								System.out.println("ERROR IN ALP");
						}
					}
				}
			}


		}
		catch(FileNotFoundException fn)
		{
			fn.printStackTrace();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

		catch(NumberFormatException nf)
		{
			nf.printStackTrace();
		}
		finally
		{
			inp.close();
			output.close();
		}
	}
	
	void searchPot1(String str[]) throws IOException
	{
		if(str.length!=1)
		{
			if(str[1].equalsIgnoreCase("START"))
			{
				st[stc][0]=str[0];
				st[stc][1]=lc+"";
				st[stc][2]=str[2];
				st[stc][3]="1";
				st[stc++][4]="R";
			}
			else if(str[1].equalsIgnoreCase("DS"))
			{
				int length;

				st[stc][0]=str[0];
				st[stc][1]=lc+"";
				st[stc][2]="";
				st[stc][4]="R";
				if(str[2].indexOf("F")!=0)
				{
					length=Integer.parseInt(str[2].substring(0,str[2].indexOf("F")));
					length=4*length;
					st[stc++][3]=length+"";
					prelc=lc;
					lc=lc+length;
				}
				else
				{
					st[stc++][3]="4";
					prelc=lc;
					lc=lc+4;
				}

			}
			else if(str[1].equalsIgnoreCase("DC"))
			{
				int length=0;
				StringTokenizer sy=new StringTokenizer(str[2],",");
				String syval[]=new String[sy.countTokens()];
				for(int i=0;i<syval.length;i++)
					syval[i]=sy.nextToken();

				st[stc][0]=str[0];
				st[stc][1]=lc+"";
				st[stc][2]=str[2];
				st[stc][4]="R";
				prelc=lc;
				for(int i=0;i<syval.length;i++)
					length=length+4;

				st[stc++][3]=length+"";
				lc=lc+length;
			}
			else if(str[1].equalsIgnoreCase("EQU"))
			{
				st[stc][0]=str[0];
				if(str[2].equals("*"))
				{
					st[stc][1]=lc+"";
					st[stc][2]="0";
					st[stc][4]="R";
				}
				else
				{
					st[stc][1]=str[2];
					st[stc][2]="0";
					st[stc][4]="A";
				}
				st[stc++][3]="1";
			}
		}

		if(str[0].equalsIgnoreCase("LTORG"))
		{
			int j;
			lc=prelc+8;
			for(int i=0;i<ltc;i++)
			{
				if(lt[i][1].equals(""))
				{
					lt[i][1]=(lc)+"";
					if(lt[i][0].indexOf("F")==-1)
					{
						lt[i][2]="";
					}
					else
					{
						lt[i][2]=lt[i][0];
					}
					prelc=lc;
					lc=lc+4;
				}
			}
		}


		else if(str[0].equalsIgnoreCase("END"))
		{
			int j;
			for(int i=0;i<ltc;i++)
			{
				if(lt[i][1].equals(""))
				{
					lt[i][1]=lc+"";
					if(lt[i][0].indexOf("F")==-1)
					{
						lt[i][2]="";
					}
					else
					{
						lt[i][2]=lt[i][0];
					}
					prelc=lc;
					lc=lc+4;
				}
				if(lt[i][2].equals(""))
				{
					int index=lt[i][0].indexOf(")");
					for(j=0;j<stc;j++)
						if(lt[i][0].substring(2,index).equals(st[j][0]))
						{
							lt[i][2]="F'"+st[j][1]+"'";
							break;
						}
				}
			}
			
			//pass2();
		}
		else if(str[0].equalsIgnoreCase("USING"))
		{
			output.write(s);
			output.newLine();
		}
		else if(str[0].equalsIgnoreCase("DROP"))
		{
			output.write(s);
			output.newLine();
		}
	}

			
			

	void searchMot1(String str[],int i) throws IOException
	{
		int length;
		length=Integer.parseInt(mot[i][3]);

		StringTokenizer stn=new StringTokenizer(str[1],",");
		if(stn.countTokens()!=1)
		{
			String string[]=new String[stn.countTokens()];
			for(i=0;i<string.length;i++)
				string[i]=stn.nextToken();
						
			if(string[1].substring(0,1).equals("="))
			{
				lt[ltc][0]=string[1].substring(1);
				lt[ltc][1]="";
				lt[ltc][2]="";
				lt[ltc][3]="4";
				lt[ltc++][4]="R";
			}
		}
		output.write(s);
		output.newLine();
		prelc=lc;
		lc=lc+length;
	}

	void pass2() throws IOException
	{
		int i,j,x;
		boolean flag=false;
		lc=0;
		try
		{
			p2inp=new BufferedReader(new FileReader("asspass1_output.txt"));
			File op=new File("asspass2_output.txt");
			if(!op.exists())
				op.createNewFile();
			fw2=new FileWriter(op.getAbsoluteFile());
			p2op=new BufferedWriter(fw2);
			
			while((s=p2inp.readLine())!=null)
			{
				StringTokenizer stn=new StringTokenizer(s);
				String str[]=new String[stn.countTokens()];
				
				for(i=0;i<str.length;i++)
					str[i]=stn.nextToken();

				for(i=0;i<pot.length;i++)
					if(str[0].equalsIgnoreCase(pot[i]))
						break;
				if(i!=pot.length)
					searchPot2(str);

				else
				{
					for(i=0;i<mot.length;i++)
						if(str[0].equalsIgnoreCase(mot[i][0]))
							break;

					if(i!=mot.length)
					{
						if(mot[i][1].equals("RR"))
						{
							prelc=lc;
							lc=lc+2;
							searchMot2(str,i);
						}
						else
						{
							prelc=lc;
							lc=lc+4;
							searchMot2(str,i);
						}
					}

					else
					{
						for(i=0;i<mot.length;i++)
							if(str[1].equalsIgnoreCase(mot[i][0]))
								break;
						
						if(i!=mot.length)
						{
							String string[]=new String[str.length-1];
							for(j=1;j<str.length;j++)
								string[j-1]=str[j];
							if(mot[i][1].equals("RR"))
							{
								prelc=lc;
								lc=lc+2;
								searchMot2(string,i);
							}
							else
							{
								prelc=lc;
								lc=lc+4;
								searchMot2(string,i);
							}
						}
						else
						{
							for(i=0;i<pot.length;i++)
								if(str[1].equalsIgnoreCase(pot[i]))
									break;

							if(i!=pot.length)
								searchPot2(str);

							else
								System.out.println("There is an error in alp");
						}
					}
				}
				for(i=0;i<stc;i++)
					if((lc==Integer.parseInt(st[i][1])) && (!st[i][2].equals("0")))
					{
						p2op.write(lc+st[i][2]);
						p2op.newLine();
						prelc=lc;
						lc=lc+Integer.parseInt(st[i][3]);
						flag=true;
					}

				for(j=0;j<ltc;j++)
					if(lc==Integer.parseInt(lt[j][1]) ||  (prelc+8)==Integer.parseInt(lt[j][1]))
					{
						if((prelc+8)==Integer.parseInt(lt[j][1]))
							lc=prelc+8;

						p2op.write(lc+"\t"+lt[j][2]);
						p2op.newLine();
						prelc=lc;
						lc=lc+Integer.parseInt(lt[j][3]);
					}

				if(!flag)
				{
				for(i=0;i<stc;i++)
					if((lc==Integer.parseInt(st[i][1])) && (!st[i][2].equals("0")))
					{
						p2op.write(lc+"\t"+st[i][2]);
						p2op.newLine();
						prelc=lc;
						lc=lc+Integer.parseInt(st[i][3]);
						flag=true;
					}
				}
			}

		}
		catch(FileNotFoundException fn)
		{
			fn.printStackTrace();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}

		catch(NumberFormatException nf)
		{
			nf.printStackTrace();
		}
		finally
		{
			p2inp.close();
			p2op.close();
		}
	}
	
	void searchMot2(String str[],int i) throws IOException
	{
		String string="",a;
		int offset=-1,j,k,l,x=1;
		StringTokenizer stok=new StringTokenizer(str[1],",");
		String stk[]=new String[stok.countTokens()];

		for(j=0;j<stk.length;j++)
			stk[j]=stok.nextToken();

		if(str[0].equalsIgnoreCase("BNE"))
		{
			x=0;
			string=prelc+"\t"+"BC"+"\t"+7+",";
		}

		else if(str[0].equalsIgnoreCase("BR"))
		{
			x=0;
			string=prelc+"\t"+"BCR"+"\t"+15+",";
		}

		else
		{
		for(j=0;j<stc;j++)
			if(stk[0].equalsIgnoreCase(st[j][0]))
			{
				string=prelc+"\t"+str[0]+"\t"+st[j][1]+",";
				break;
			}
		
		if(j==stc)
			string=prelc+"\t"+str[0]+"\t"+stk[0]+",";
		}
		
		if(stk[x].indexOf("(")!=-1)
			a=stk[x].substring(0,stk[x].indexOf("("));

		else
			a=stk[x];

		for(j=0;j<stc;j++)
		{
			if(a.equalsIgnoreCase(st[j][0]))
			{
				if(st[j][4].equalsIgnoreCase("R"))
				{
					if(stk[x].indexOf("(")==-1)
					{
						for(k=btc-1;k>=0 && offset<0;k--)
							offset=Integer.parseInt(st[j][1])-Integer.parseInt(bt[k][1]);

						string=string+Integer.toString(offset)+"(0,"+bt[k+1][0]+")";
					}
					else
					{
						for(k=btc-1;k>=0 && offset<0;k--)
							offset=Integer.parseInt(st[j][1])-Integer.parseInt(bt[k][1]);
						
						for(l=0;l<stc;l++)
							if(stk[x].substring(stk[x].indexOf("(")+1,stk[x].indexOf(")")).equalsIgnoreCase(st[l][0]))
							{
								string=string+Integer.toString(offset)+"("+st[l][1]+","+bt[k+1][0]+")";
								break;
							}
						if(l==stc)
							string=string+Integer.toString(offset)+"("+stk[x].substring(stk[x].indexOf("(")+1,stk[x].indexOf(")"))+","+bt[k-1][0]+")";	
					}
				}
				else
				{
					string=string+st[j][1];
				}
				break;
			}
		}

		if(j==stc)
		{
			for(k=0;k<ltc;k++)
				if(stk[x].substring(1).equalsIgnoreCase(lt[k][0]))
				{
					for(l=btc-1;l>=0 && offset<0;l--)
						offset=Integer.parseInt(lt[k][1])-Integer.parseInt(bt[l][1]);
					string=string+Integer.toString(offset)+"(0,"+bt[l+1][0]+")";
					break;
				}

			if(k==ltc)
				string=string+stk[x];
		}
		p2op.write(string);
		p2op.newLine();
	}
	
	void searchPot2(String str[]) throws IOException
	{
		int i,x=-1;
		String a="";
		if(str[0].equalsIgnoreCase("USING"))
		{
			StringTokenizer stok=new StringTokenizer(str[1],",");
			String stk[]=new String[stok.countTokens()];
			
			for(i=0;i<stk.length;i++)
				stk[i]=stok.nextToken();

			for(i=0;i<stc;i++)
				if(stk[1].equalsIgnoreCase(st[i][0]))
				{
					a=st[i][1];
					break;
				}
			
			if(i==stc)
				a=stk[1];

			for(i=0;i<btc;i++)
				if(a.equalsIgnoreCase(bt[i][0]))
					x=i;
					

			if(stk[0].equalsIgnoreCase("*"))
			{
				if(x==-1)
					bt[btc][1]=lc+"";
				else
					bt[x][1]=lc+"";
			}
			
			else
			{
				for(i=0;i<stc;i++)
					if(stk[0].equalsIgnoreCase(st[i][0]))
					{
						if(x==-1)
							bt[btc][1]=st[i][1];

						else
							bt[x][1]=st[i][1];
						break;
					}

				if(i==stc)
				{
					if(x==-1)
						bt[btc][1]=stk[0];
					else
						bt[x][1]=stk[0];
				}
			}

			for(i=0;i<stc;i++)
				if(stk[1].equalsIgnoreCase(st[i][0]))
				{
					if(x==-1)
						bt[btc++][0]=st[i][1];
					break;
				}

			if(i==stc)
			{
				if(x==-1)
				bt[btc++][0]=stk[1];
			}
		}

		if(str[0].equalsIgnoreCase("DROP"))
		{
			int j;
			String s;
			for(i=0;i<stc;i++)
				if(str[1].equalsIgnoreCase(st[i][0]))
					break;

			if(i==stc)
				s=str[1];
			else
				s=st[i][1];

			for(j=0;j<btc;j++)
				if(s.equalsIgnoreCase(bt[j][0]))
					break;
			
			for(i=j;i<btc;i++)
			{
				bt[i][0]=bt[i+1][0];
				bt[i][1]=bt[i+1][1];
			}

		}
	}

	void display(String a[][],int m,int n,String s) throws IOException
	{	
		int i,j;
		File sym=new File(s);
		if(!sym.exists())
			sym.createNewFile();
		BufferedWriter f=new BufferedWriter(new FileWriter(sym.getAbsoluteFile()));
		for(i=0;i<m;i++)
		{
			for(j=0;j<n;j++)
			{
				System.out.print(a[i][j]+"\t");
				f.write(a[i][j]+"\t");
			}
			System.out.println();
			f.newLine();	
		}
		f.close();
	}		

	public static void main(String args[])
	{
		
		try
		{

			Assembler as=new Assembler("inp.txt");
			as.pass1();
			as.pass2();

			System.out.println("Symbol Table");
			as.display(as.st,as.stc,5,"Symble_table");

			System.out.println("Literal Table");
			as.display(as.lt,as.ltc,5,"Literal_table");
		
			System.out.println("Base Table");
			as.display(as.bt,as.btc,2,"Base_table");
		
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}												
}		
	