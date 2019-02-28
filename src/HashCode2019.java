import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alex
 * @Team Myself, Charles Clarke, Andrew Dunnings, and Oliver Masters.
 */
public class HashCode2019
{
	/* File to deal with —— { A = a_example, B = b_lovely_landscapes, C = c_memorable_moments, D = d_pet_pictures, E = e_shiny_selfies... }. */

	private static final HashcodeFile file = HashcodeFile.E;

	private static int ITER_LIMIT = 100;

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
		Slide firstSlide, lastSlide;

		for ( int i = 0;; i++ )
		{
			if ( pictures.get( i ).isHorizontal() )
			{
				slideAssignment.add( new HorizontalSlide( pop( pictures, i ) ) );
				break;
			}
			else if ( i == pictures.size() - 1 )
			{
				slideAssignment.add( new VerticalSlide( pictures.get( 0 ), pictures.get( 1 ) ) );
				pictures.remove( 0 );
				pictures.remove( 1 );
				break;
			}
		}

		Picture p, p2;
		int maxScore, newScore;
		Slide bestSlide = null, newSlidePossibility;
		boolean bestAtFirstSlide = false;

		while ( pictures.size() > 0 )
		{
			System.out.println( pictures.size() );

			maxScore = Integer.MIN_VALUE;

			for ( int i = 0; i < pictures.size(); i++ )
			{
				p = pictures.get( i );

				firstSlide = slideAssignment.get( 0 );
				lastSlide = slideAssignment.get( slideAssignment.size() - 1 );

				if ( p.isHorizontal() )
				{
					newSlidePossibility = new HorizontalSlide( p );

					newScore = score( newSlidePossibility, firstSlide );
					if ( newScore > maxScore )
					{
						maxScore = newScore;
						bestSlide = newSlidePossibility;
						bestAtFirstSlide = true;
					}

					newScore = score( newSlidePossibility, lastSlide );
					if ( newScore > maxScore )
					{
						maxScore = newScore;
						bestSlide = newSlidePossibility;
						bestAtFirstSlide = false;
					}
				}
				else if ( p.isVertical() )
				{
					for ( int j = i + 1; j < pictures.size(); j++ )
					{
						p2 = pictures.get( j );

						if ( p.equals( p2 ) || p2.isHorizontal() )
						{
							continue;
						}

						newSlidePossibility = new VerticalSlide( p, p2 );

						newScore = score( newSlidePossibility, firstSlide );
						if ( newScore > maxScore )
						{
							maxScore = newScore;
							bestSlide = newSlidePossibility;
							bestAtFirstSlide = true;
						}

						newScore = score( newSlidePossibility, lastSlide );
						if ( newScore > maxScore )
						{
							maxScore = newScore;
							bestSlide = newSlidePossibility;
							bestAtFirstSlide = false;
						}

						if ( j > ITER_LIMIT )
						{
							break;
						}
					}
				}

				if ( i > ITER_LIMIT )
				{
					break;
				}

			}

			slideAssignment.add( bestAtFirstSlide ? 0 : slideAssignment.size(), bestSlide );

			for ( Picture pic : bestSlide.getPictures() )
			{
				pictures.remove( pic );
			}

			if ( pictures.size() == 1 )
			{
				if ( pictures.get( 0 ).isVertical() )
				{
					break;
				}
			}
		}

		return slideAssignment;
	}

	public static Picture pop ( List<Picture> pictures, int index )
	{
		Picture p = pictures.get( index );
		pictures.remove( index );
		return p;
	}

	private static int score ( Slide s1, Slide s2 )
	{
		return score( s1.getTags(), s2.getTags() );
	}

	private static int score ( Set<String> tags1, Set<String> tags2 )
	{
		int s1 = setMinusScore( tags1, tags2 );
		int s2 = setMinusScore( tags2, tags1 );

		int score = ( tags1.size() + tags2.size() - s1 - s2 ) / 2;

		score = Math.min( score, s1 );
		score = Math.min( score, s2 );

		return score;
	}

	private static int intersectionScore ( Set<String> tags1, Set<String> tags2 )
	{
		Set<String> tempTags = new HashSet<String>( tags1 );
		tempTags.addAll( tags2 );

		return tempTags.size();
	}

	/* Gets number of tags only in tags1 (first parameter). */
	private static int setMinusScore ( Set<String> tags1, Set<String> tags2 )
	{
		int score = 0;

		for ( String element : tags1 )
		{
			if ( !tags2.contains( element ) )
			{
				score += 1;
			}
		}
		return score;
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
		B ( "b_lovely_landscapes" ),
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
	public abstract Set<String> getTags ();

	@Override
	public abstract String toString ();

	public abstract List<Picture> getPictures ();
}

class HorizontalSlide extends Slide
{
	public final Picture		p;
	public final Set<String>	tags;

	public HorizontalSlide( Picture p )
	{
		this.p = p;
		this.tags = p.tags;
	}

	@Override
	public Set<String> getTags ()
	{
		return tags;
	}

	@Override
	public List<Picture> getPictures ()
	{
		return Arrays.asList( p );
	}

	@Override
	public String toString ()
	{
		return Integer.toString( p.id );
	}
}

class VerticalSlide extends Slide
{
	public final Picture		p1;
	public final Picture		p2;
	public final Set<String>	tags;

	public VerticalSlide( Picture p1, Picture p2 )
	{
		this.p1 = p1;
		this.p2 = p2;
		this.tags = new HashSet<String>( p1.tags );
		this.tags.addAll( p2.tags );
	}

	@Override
	public Set<String> getTags ()
	{
		return tags;
	}

	@Override
	public List<Picture> getPictures ()
	{
		return Arrays.asList( p1, p2 );
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
	public final Set<String>	tags;

	Picture( int id, Orientation orientation, String[] tags )
	{
		this.id = id;
		this.orientation = orientation;
		this.tags = new HashSet<String>( Arrays.asList( tags ) );
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
