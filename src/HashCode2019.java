import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alex
 * @Team Myself, Charles Clarke, Andrew Dunnings, and Oliver Masters.
 */
public class HashCode2019
{
	/* File to deal with —— { A = a_example, B = b_lovely_landscape, C = c_memorable_moments, D = d_pet_pictures, E = e_shiny_selfies... }. */

	private static final HashcodeFile file = HashcodeFile.A;

	public static void main ( String[] args )
	{
		List<Picture> input = getInput( file );
		List<Slide> solution = getAssignment( input );
		output( file, solution );
	}

	/**
	 * Gets the assignment of cars, indexed by ints, to the list of rides it will give, also index by ints.
	 *
	 * @param scoreFunc The function that calculates the score of a ride for a car — used when greedily trying to assign rides to cars.
	 * @return A list which has, indexed by the integer value of the corresponding car, the rides ids the car has been allocated.
	 */
	private static List<Slide> getAssignment ( List<Picture> pictures )
	{
		List<Slide> slideAssignment = new ArrayList<Slide>();

		return null;
	}

	private static List<Picture> getInput ( HashcodeFile file )
	{
		String[] inputLine;
		List<Picture> pictures = new ArrayList<Picture>();
		Orientation o;
		String[] tags;

		try ( BufferedReader br = new BufferedReader( new FileReader( file.getInputPath().toFile() ) ) )
		{
			int N = Integer.parseInt( br.readLine() );

			for ( int i = 0; i < N; i++ )
			{
				inputLine = br.readLine().trim().split( "\\s+" );
				o = Orientation.strToOrientation( inputLine[ 0 ] );
				tags = Arrays.copyOfRange( inputLine, 1, inputLine.length );

				pictures.add( new Picture( i, o, tags ) );
			}

			/* Input here */

		}
		catch ( IOException e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}

		return pictures;
	}

	/** Output solution to file with filename given. */
	private static void output ( HashcodeFile file, List<Slide> solution )
	{
		try ( BufferedWriter bw = new BufferedWriter( new FileWriter( file.getOutputPath().toFile() ) ) )
		{
			bw.write( solution.size() + "\n" );
			for ( Slide slide : solution )
			{
				bw.write( slide.toString() + '\n' );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			System.exit( 1 );
		}
	}

	private enum HashcodeFile
	{
		A ( "a_example" ),
		B ( "b_lovely_landscape" ),
		C ( "c_memorable_moments" ),
		D ( "d_pet_pictures" ),
		E ( "e_shiny_selfies" );

		public String str;

		HashcodeFile( String str )
		{
			this.str = str;
		}

		Path getInputPath ()
		{
			return Paths.get( "input", str + ".in" );
		}

		Path getOutputPath ()
		{
			return Paths.get( "output", str + ".output" );
		}
	}
}

abstract class Slide
{
	public String toString;
}

class HorizontalSlide extends Slide
{
	public final Picture p;

	public HorizontalSlide( Picture p )
	{
		this.p = p;
	}

	@Override
	public String toString ()
	{
		return Integer.toString( p.id );
	}
}

class VerticalSlide extends Slide
{
	public final Picture	p1;
	public final Picture	p2;

	public VerticalSlide( Picture p1, Picture p2 )
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public String toString ()
	{
		return Integer.toString( p1.id ) + " " + Integer.toString( p2.id );
	}
}

class Picture
{
	public final int			id;
	public final Orientation	orientation;
	public final String[]		tags;

	Picture( int id, Orientation orientation, String[] tags )
	{
		this.id = id;
		this.orientation = orientation;
		this.tags = tags;
	}

	public boolean isVertical ()
	{
		return orientation.equals( Orientation.V );
	}

	public boolean isHorizontal ()
	{
		return orientation.equals( Orientation.H );
	}
}

enum Orientation
{
	V,
	H;

	public static Orientation strToOrientation ( String str )
	{
		switch ( str )
		{
			case "V":
				return Orientation.V;
			case "H":
				return Orientation.H;
			default:
				return null;
		}
	}
}
