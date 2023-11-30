//######################################################################################
/**
 * This support class provides many general matrix manipulation functions, as
 * well as a number of specialised matrix operations pertaining to Petri net
 * analysis.
 * 
 * @author Manos Papantoniou & Michael Camacho
 * @version February 2004
 * 
 * Based on the Jama Matrix class, the PNMatrix class offers a small subset of
 * the operations, and is used for matrices of integers only, as required by the
 * petri net analyser project.
 * 
 * <P>
 * This Class provides the fundamental operations of numerical linear algebra.
 * Various constructors create Matrices from two dimensional arrays of integer
 * numbers. Various "gets" and "sets" provide access to submatrices and matrix
 * elements. Several methods implement basic matrix arithmetic, including matrix
 * addition and multiplication, and element-by-element array operations. Methods
 * for reading and printing matrices are also included.
 * <P>
 * 
 * @author Edwin Chung a new boolean attribute was added (6th Feb 2007)
 */
// ######################################################################################
package pipe.common.dataLayer;
// ######################################################################################
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

// ######################################################################################
public class PNMatrix
{

	// ######################################################################################
	/**
	 * Construct a matrix from a copy of a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of integers.
	 * @return The copied matrix.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 */
	public static PNMatrix constructWithCopy(final int[][] A)
	{
		final int m = A.length;
		final int n = A[0].length;
		final PNMatrix X = new PNMatrix(m, n);
		final int[][] C = X.getArray();
		for (int i = 0; i < m; i++)
		{
			if (A[i].length != n)
			{
				throw new IllegalArgumentException("All rows must have the same length.");
			}
			for (int j = 0; j < n; j++)
			{
				C[i][j] = A[i][j];
			}
		}
		return X;
	}

	// ######################################################################################
	/**
	 * Generate identity matrix]
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @return An m-by-n matrix with ones on the diagonal and zeros elsewhere.
	 */
	public static PNMatrix identity(final int m, final int n)
	{
		final PNMatrix A = new PNMatrix(m, n);

		final int[][] X = A.getArray();
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				X[i][j] = i == j ? 1 : 0;
			}
		}
		return A;
	}

	/**
	 * Array for internal storage of elements.
	 * 
	 * @serial internal array storage.
	 */
	private int[][]	A;

	/**
	 * Row and column dimensions.
	 * 
	 * @serial row dimension.
	 * @serial column dimension.
	 */
	private int		m, n;

	/** Used to determine whether the matrixes have been modified */
	public boolean	matrixChanged;

// ######################################################################################
	/**
	 * Construct an m-by-n matrix of zeros.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */
	public PNMatrix(final int m, final int n) {
		this.m = m;
		this.n = n;
		this.A = new int[m][n];
		this.matrixChanged = true;
	}

// ######################################################################################
	/**
	 * Construct a matrix from a one-dimensional packed array
	 * 
	 * @param vals
	 *            One-dimensional array of integers, packed by columns (ala
	 *            Fortran).
	 * @param m
	 *            Number of rows.
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m.
	 */
	public PNMatrix(final int vals[], final int m) {
		this.m = m;
		this.matrixChanged = true;
		this.n = m != 0 ? vals.length / m : 0;
		if (m * this.n != vals.length)
		{
			throw new IllegalArgumentException("Array length must be a multiple of m.");
		}
		this.A = new int[m][this.n];
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = vals[i + j * m];
			}
		}
	}

// ######################################################################################
	/**
	 * Construct an m-by-n constant matrix.
	 * 
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 * @param s
	 *            Fill the matrix with this scalar value.
	 */
	public PNMatrix(final int m, final int n, final int s) {
		this.m = m;
		this.n = n;
		this.A = new int[m][n];
		this.matrixChanged = true;
		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				this.A[i][j] = s;
			}
		}
	}

// ######################################################################################
	/**
	 * Construct a matrix from a 2-D array.
	 * 
	 * @param A
	 *            Two-dimensional array of integers.
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */
	public PNMatrix(final int[][] A) {
		if (A != null)
		{
			this.m = A.length;
			if (A.length >= 1)
			{
				this.n = A[0].length;
				for (int i = 0; i < this.m; i++)
				{
					if (A[i].length != this.n)
					{
						throw new IllegalArgumentException("All rows must have the same length.");
					}
				}
				this.A = A;
				this.matrixChanged = true;
			}
		}
	}

// ######################################################################################
	/**
	 * Construct a matrix quickly without checking arguments.
	 * 
	 * @param A
	 *            Two-dimensional array of integers.
	 * @param m
	 *            Number of rows.
	 * @param n
	 *            Number of colums.
	 */
	public PNMatrix(final int[][] A, final int m, final int n) {
		this.A = A;
		this.m = m;
		this.n = n;
		this.matrixChanged = true;
	}

// ######################################################################################
	/**
	 * Append a column matrix (vector) to the right of another matrix.
	 * 
	 * @param X
	 *            Column matrix (vector) to append.
	 * @return The matrix with the column vector appended to the right.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public PNMatrix appendVector(final PNMatrix X)
	{
		final PNMatrix R = new PNMatrix(this.m, this.n + 1);
		// the extended matrix
		R.setMatrix(0, this.m - 1, 0, this.n - 1, this);

		try
		{

			for (int i = 0; i < this.m; i++)
			{
				R.set(i, this.n, X.get(i, 0));
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Row indices incompatible");
		}
		return R;
	}

// ######################################################################################
	/**
	 * Check if a matrix has a row that satisfies the cardinality condition
	 * 1.1.b of the algorithm.
	 * 
	 * @return True if the matrix satisfies the condition and linear combination
	 *         of columns followed by column elimination is required.
	 */
	public int cardinalityCondition()
	{
		final int cardRow = -1; // a value >= 0 means either pPlus or pMinus
								// have
		// cardinality == 1
		// and it is the value of the row where this condition occurs
		// -1 means that both pPlus and pMinus have cardinality != 1
		int countpPlus = 0, countpMinus = 0;
		int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
		final int pLength = this.n, mLength = this.n;

		for (int i = 0; i < this.m; i++)
		{
			countpPlus = 0;
			countpMinus = 0;
			pPlus = this.getPositiveIndices(i); // get +ve indices of ith row
			pMinus = this.getNegativeIndices(i); // get -ve indices of ith
													// row
			for (int j = 0; j < pLength; j++)
			{
				if (pPlus[j] != 0)
				{
					countpPlus++;
				}
			}
			for (int j = 0; j < mLength; j++)
			{
				if (pMinus[j] != 0)
				{
					countpMinus++;
				}
			}
			if (countpPlus == 1 || countpMinus == 1)
			{
				return i;
			}
		}
		return cardRow;
	}

// ######################################################################################
	/**
	 * Find the column index of the element in the pPlus or pMinus set, where
	 * pPlus or pMinus has cardinality == 1.
	 * 
	 * @return The column index, -1 if unsuccessful (this shouldn't happen under
	 *         normal operation).
	 */
	public int cardinalityOne()
	{
		final int k = -1; // the col index of cardinality == 1 element

		int countpPlus = 0, countpMinus = 0;
		int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
		final int pLength = this.n, mLength = this.n;

		for (int i = 0; i < this.m; i++)
		{
			countpPlus = 0;
			countpMinus = 0;
			pPlus = this.getPositiveIndices(i); // get +ve indices of ith row
			pMinus = this.getNegativeIndices(i); // get -ve indices of ith
													// row
			for (int j = 0; j < pLength; j++)
			{
				if (pPlus[j] != 0)
				{
					countpPlus++;
				}
			}
			for (int j = 0; j < mLength; j++)
			{
				if (pMinus[j] != 0)
				{
					countpMinus++;
				}
			}
			if (countpPlus == 1)
			{
				return pPlus[0] - 1;
			}
			if (countpMinus == 1)
			{
				return pMinus[0] - 1;
			}
		}

		return k;
	}

// ######################################################################################
	/**
	 * Check if a matrix satisfies condition 1.1 of the algorithm.
	 * 
	 * @return True if the matrix satisfies the condition and column elimination
	 *         is required.
	 */
	public boolean checkCase11()
	{
		final boolean satisfies11 = false; // true means there is an empty set
		// pPlus or pMinus
		// false means that both pPlus and pMinus are non-empty
		boolean pPlusEmpty = true, pMinusEmpty = true;
		int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
		final int m = this.getRowDimension();

		for (int i = 0; i < m; i++)
		{
			pPlusEmpty = true;
			pMinusEmpty = true;
			pPlus = this.getPositiveIndices(i); // get +ve indices of ith row
			pMinus = this.getNegativeIndices(i); // get -ve indices of ith
													// row
			final int pLength = pPlus.length, mLength = pMinus.length;

			for (int j = 0; j < pLength; j++)
			{
				if (pPlus[j] != 0)
				{
					// (non-empty set)
					pPlusEmpty = false;
				}
			}
			for (int j = 0; j < mLength; j++)
			{
				if (pMinus[j] != 0)
				{
					// (non-empty set)
					pMinusEmpty = false;
				}
			}
			// if there is an empty set and it is not a zeros-only row then
			// column elimination is possible
			if ((pPlusEmpty || pMinusEmpty) && !this.isZeroRow(i))
			{
				return true;
			}
			// reset pPlus and pMinus to 0
			for (int j = 0; j < pLength; j++)
			{
				pPlus[j] = 0;
			}

			for (int j = 0; j < mLength; j++)
			{
				pMinus[j] = 0;
			}
		}
		return satisfies11;
	}

// ######################################################################################
	/**
	 * Throws IllegalArgumentException if dimensions of A and B differ.
	 * 
	 * @param B
	 *            The matrix to check the dimensions.
	 */
	private void checkMatrixDimensions(final PNMatrix B)
	{
		if (B.m != this.m || B.n != this.n)
		{
			throw new IllegalArgumentException("Matrix dimensions must agree.");
		}
	}

// ######################################################################################
	/**
	 * Clone the IntMatrix object.
	 * 
	 * @return The clone of the current matrix.
	 */
	@Override
	public Object clone()
	{
		return this.copy();
	}

// ######################################################################################
	/**
	 * Find the comlumn indices to be changed by linear combination.
	 * 
	 * @return An array of integers, these are the indices increased by 1 each.
	 */
	public int[] colsToUpdate()
	{
		final int js[] = null; // An array of integers with the comlumn indices
								// to
		// be changed by linear combination.
		// the col index of cardinality == 1 element

		int countpPlus = 0, countpMinus = 0;
		int[] pPlus, pMinus; // arrays containing the indices of +ve and -ve
		final int pLength = this.n, mLength = this.n;

		for (int i = 0; i < this.m; i++)
		{
			countpPlus = 0;
			countpMinus = 0;
			pPlus = this.getPositiveIndices(i); // get +ve indices of ith row
			pMinus = this.getNegativeIndices(i); // get -ve indices of ith
													// row
			for (int j = 0; j < pLength; j++)
			{
				if (pPlus[j] != 0)
				{
					countpPlus++;
				}
			}
			for (int j = 0; j < mLength; j++)
			{
				if (pMinus[j] != 0)
				{
					countpMinus++;
				}
			}
			// if pPlus has cardinality ==1 return all the elements in pMinus
			// reduced by 1 each
			if (countpPlus == 1)
			{
				return pMinus;
			}
			else if (countpMinus == 1)
			{
				return pPlus;
			}

		}

		return js;
	}

// ######################################################################################
	/**
	 * Make a deep copy of a matrix
	 * 
	 * @return The matrix copy.
	 */
	public PNMatrix copy()
	{
		final PNMatrix X = new PNMatrix(this.m, this.n);
		final int[][] C = X.getArray();
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[i][j] = this.A[i][j];
			}
		}
		return X;
	}

// ######################################################################################
	/**
	 * Divide a matrix by an int in place, A = s*A
	 * 
	 * @param s
	 *            int divisor
	 * @return replace A by A/s
	 */
	public PNMatrix divideEquals(final int s)
	{
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = this.A[i][j] / s;
			}
		}
		return this;
	}

// ######################################################################################
	/**
	 * Eliminate a column from the matrix, column index is toDelete
	 * 
	 * @param toDelete
	 *            The column number to delete.
	 * @return The matrix with the required row deleted.
	 */
	public PNMatrix eliminateCol(final int toDelete)
	{
		final int m = this.getRowDimension(), n = this.getColumnDimension();
		PNMatrix reduced = new PNMatrix(m, n);
		final int[] cols = new int[n - 1]; // array of cols which will not be
		// eliminated
		int count = 0;

		// find the col numbers which will not be eliminated
		for (int i = 0; i < n; i++)
		{
			// if an index will not be eliminated, keep it in the new array cols
			if (i != toDelete)
			{
				cols[count++] = i;
			}
		}
		// System.out.print("Eliminating column " + toDelete + " from matrix
		// below... keeping columns ");
		// printArray(cols);
		// print(2, 0);
		// System.out.println("Reduced matrix");
		reduced = this.getMatrix(0, m - 1, cols);
		// reduced.print(2, 0);

		return reduced;
	}

// ######################################################################################
	/**
	 * Find a column with non-minimal support.
	 * 
	 * @return The column index that has non-minimal support, -1 if there is
	 *         none.
	 */
	public int findNonMinimal()
	{
		final int k = -1; // the non-minimal support column index
		final int m = this.getRowDimension(), n = this.getColumnDimension();

		PNMatrix X = new PNMatrix(m, 1); // column one, represents first col
		// of comparison
		PNMatrix Y = new PNMatrix(m, 1); // col two, represents rest columns
		// of comparison
		PNMatrix Z = new PNMatrix(m, 1); // difference column 1 - column 2

		for (int i = 0; i < n; i++)
		{
			X = this.getMatrix(0, m - 1, i, i);
			for (int j = 0; j < n; j++)
			{
				if (i != j)
				{
					Y = this.getMatrix(0, m - 1, j, j);
					Z = X.minus(Y);
					// if there is at least one -ve element then break inner
					// loop
					// and try another Y vector (because X is minimal with
					// respect to Y)
					// if there is no -ve element then return from the function
					// with index of X
					// to be eliminated, because X is not minimal with respect
					// to Y
					if (!Z.hasNegativeElements())
					{
						return i;
					}
				}
			}
		}

		return k;
		// compare each columns' set with the other columns

		// if you find during comparison with another another column that it has
		// one more element, stop the comparison (the current col cannot be
		// eliminated
		// based on this comparison) and go to the next comparison

		// if you find that the col in question has all the elements of another
		// one
		// then eliminate the col in question
	}

// ######################################################################################
	/**
	 * Find the coefficients corresponding to column indices of all but the
	 * first non zero elements of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero coefficients in
	 * @return Array of ints of coefficients of all but the first non-zero
	 *         elements of row h.
	 */
	public int[] findRemainingNZCoef(final int h)
	{
		final int n = this.getColumnDimension();
		final int[] k = new int[n];
		int count = 0; // increases as we add new indices in the array of ints
		int anElement; // an element of the matrix

		for (int j = 1; j < n; j++)
		{
			if ((anElement = this.get(h, j)) != 0)
			{
				k[count++] = anElement;
			}
		}
		return k;
	}

// ######################################################################################
	/**
	 * Find the column indices of all but the first non zero elements of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero element in
	 * @return Array of ints of column indices (starting from 0 for 1st column)
	 *         of all but the first non-zero elements of row h.
	 */
	public int[] findRemainingNZIndices(final int h)
	{
		final int n = this.getColumnDimension();
		final int[] k = new int[n];
		int count = 0; // increases as we add new indices in the array of ints

		for (int j = 1; j < n; j++)
		{
			if (this.get(h, j) != 0)
			{
				k[count++] = j;
			}
		}
		return k;
	}

// ######################################################################################
	/**
	 * Find the column index of the first non zero element of row h.
	 * 
	 * @param h
	 *            The row to look for the non-zero element in
	 * @return Column index (starting from 0 for 1st column) of the first
	 *         non-zero element of row h, -1 if there is no such column.
	 */
	public int firstNonZeroElementIndex(final int h)
	{
		final int n = this.getColumnDimension();
		final int k = -1;

		for (int j = 0; j < n; j++)
		{
			if (this.get(h, j) != 0)
			{
				return j;
			}
		}
		return k;
	}

// ######################################################################################
	/**
	 * Find the first non-zero row of a matrix.
	 * 
	 * @return Row index (starting from 0 for 1st row) of the first row from top
	 *         that is not only zeros, -1 of there is no such row.
	 */
	public int firstNonZeroRowIndex()
	{
		final int m = this.getRowDimension(), n = this.getColumnDimension();
		final int h = -1;

		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (this.get(i, j) != 0)
				{
					return i;
				}
			}
		}
		return h;
	}

// ######################################################################################
	/**
	 * Find the greatest common divisor of a column matrix (vector) of integers.
	 * 
	 * @return The gcd of the column matrix.
	 */
	public int gcd()
	{
		int gcd = this.A[0][0];

		for (int i = 1; i < this.m; i++)
		{
			if (this.A[i][0] != 0 || gcd != 0)
			{
				gcd = this.gcd2(gcd, this.A[i][0]);
			}
		}

		return gcd; // this should never be zero
	}

// ######################################################################################
	/**
	 * Find the greatest common divisor of 2 integers.
	 * 
	 * @param a
	 *            The first integer.
	 * @param b
	 *            The second integer.
	 * @return The gcd of the column
	 */
	private int gcd2(int a, int b)
	{
		int gcd;
		a = Math.abs(a);
		b = Math.abs(b);

		// ensure b > a
		if (b <= a)
		{
			final int tmp = b;
			b = a;
			a = tmp;
		}

		if (a != 0)
		{
			for (int tmp; (b %= a) != 0;)
			{
				tmp = b;
				b = a;
				a = tmp;
			}
			gcd = a;
		}
		else if (b != 0)
		{
			gcd = b;
		}
		else
		{
			// both args == 0, return 0, but this shouldn't happen
			gcd = 0;
		}
		return gcd;
	}

// ######################################################################################
	/**
	 * Get a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @return A(i,j)
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public int get(final int i, final int j)
	{
		return this.A[i][j];
	}

// ######################################################################################
	/**
	 * Access the internal two-dimensional array.
	 * 
	 * @return Pointer to the two-dimensional array of matrix elements.
	 */
	public int[][] getArray()
	{
		return this.A;
	}

// ######################################################################################
	/**
	 * Copy the internal two-dimensional array.
	 * 
	 * @return Two-dimensional array copy of matrix elements.
	 */
	public int[][] getArrayCopy()
	{
		final int[][] C = new int[this.m][this.n];
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[i][j] = this.A[i][j];
			}
		}
		return C;
	}

// ######################################################################################
	/**
	 * Get column dimension.
	 * 
	 * @return The number of columns.
	 */
	public int getColumnDimension()
	{
		return this.n;
	}

// ######################################################################################
	/**
	 * Make a one-dimensional column packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by columns.
	 */
	public int[] getColumnPackedCopy()
	{
		final int[] vals = new int[this.m * this.n];
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				vals[i + j * this.m] = this.A[i][j];
			}
		}
		return vals;
	}

// ######################################################################################
	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public PNMatrix getMatrix(final int i0, final int i1, final int j0, final int j1)
	{
		final PNMatrix X = new PNMatrix(i1 - i0 + 1, j1 - j0 + 1);
		final int[][] B = X.getArray();
		try
		{
			for (int i = i0; i <= i1; i++)
			{
				for (int j = j0; j <= j1; j++)
				{
					B[i - i0][j - j0] = this.A[i][j];
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

// ######################################################################################
	/**
	 * Get a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public PNMatrix getMatrix(final int i0, final int i1, final int[] c)
	{
		final PNMatrix X = new PNMatrix(i1 - i0 + 1, c.length);
		final int[][] B = X.getArray();
		try
		{
			for (int i = i0; i <= i1; i++)
			{
				for (int j = 0; j < c.length; j++)
				{
					B[i - i0][j] = this.A[i][c[j]];
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

// ######################################################################################
	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @return A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */

	public PNMatrix getMatrix(final int[] r, final int j0, final int j1)
	{
		final PNMatrix X = new PNMatrix(r.length, j1 - j0 + 1);
		final int[][] B = X.getArray();
		try
		{
			for (int i = 0; i < r.length; i++)
			{
				for (int j = j0; j <= j1; j++)
				{
					B[i][j - j0] = this.A[r[i]][j];
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

// ######################################################################################
	/**
	 * Get a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @return A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public PNMatrix getMatrix(final int[] r, final int[] c)
	{
		final PNMatrix X = new PNMatrix(r.length, c.length);
		final int[][] B = X.getArray();
		try
		{
			for (int i = 0; i < r.length; i++)
			{
				for (int j = 0; j < c.length; j++)
				{
					B[i][j] = this.A[r[i]][c[j]];
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
		return X;
	}

// ######################################################################################
	/**
	 * For row rowNo of the matrix received return the column indices of all the
	 * negative elements
	 * 
	 * @param rowNo
	 *            row iside the Matrix to check for -ve elements
	 * @return Integer array of indices of negative elements.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public int[] getNegativeIndices(final int rowNo)
	{
		final int n = this.getColumnDimension(); // find the number of
													// columns

		// create the single row submatrix for the required row
		try
		{
			PNMatrix A = new PNMatrix(1, n);
			A = this.getMatrix(rowNo, rowNo, 0, n - 1);

			int count = 0; // index of a negative element in the returned array
			final int[] negativesArray = new int[n];
			for (int i = 0; i < n; i++)
			{
				// initialise to zero
				negativesArray[i] = 0;
			}

			for (int i = 0; i < n; i++)
			{
				if (A.get(0, i) < 0)
				{
					negativesArray[count++] = i + 1;
				}
			}

			return negativesArray;
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	/**
	 * For row rowNo of the matrix received return the column indices of all the
	 * positive elements
	 * 
	 * @param rowNo
	 *            row iside the Matrix to check for +ve elements
	 * @return The integer array of indices of all positive elements.
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public int[] getPositiveIndices(final int rowNo)
	{
		final int n = this.getColumnDimension(); // find the number of
													// columns

		// create the single row submatrix for the required row
		try
		{
			PNMatrix A = new PNMatrix(1, n);
			A = this.getMatrix(rowNo, rowNo, 0, n - 1);

			int count = 0; // index of a positive element in the returned array
			final int[] positivesArray = new int[n];
			for (int i = 0; i < n; i++)
			{
				// initialise to zero
				positivesArray[i] = 0;
			}

			for (int i = 0; i < n; i++)
			{
				if (A.get(0, i) > 0)
				{
					positivesArray[count++] = i + 1;
				}
			}

			return positivesArray;
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	/**
	 * Get row dimension.
	 * 
	 * @return The number of rows.
	 */
	public int getRowDimension()
	{
		return this.m;
	}

// ######################################################################################
	/**
	 * Make a one-dimensional row packed copy of the internal array.
	 * 
	 * @return Matrix elements packed in a one-dimensional array by rows.
	 */
	public int[] getRowPackedCopy()
	{
		final int[] vals = new int[this.m * this.n];
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				vals[i * this.n + j] = this.A[i][j];
			}
		}
		return vals;
	}

// ######################################################################################
	/**
	 * Find if a column vector has negative elements.
	 * 
	 * @return True or false.
	 */
	public boolean hasNegativeElements()
	{
		final boolean hasNegative = false;
		final int m = this.getRowDimension();

		for (int i = 0; i < m; i++)
		{
			if (this.get(i, 0) < 0)
			{
				return true;
			}
		}

		return hasNegative;
	}

// ######################################################################################
	/**
	 * Find if a matrix of invariants is covered.
	 * 
	 * @return true if it is covered, false otherwise.
	 */
	public boolean isCovered()
	{
		final boolean isCovered = true;
		// if there is an all-zeros row then it is not covered
		for (int i = 0; i < this.m; i++)
		{
			if (this.isZeroRow(i) || this.transpose().hasNegativeElements())
			{
				return false;
			}
		}
		return isCovered;
	}

// ######################################################################################
	/**
	 * Check if a matrix is all zeros.
	 * 
	 * @return true if all zeros, false otherwise
	 */
	public boolean isZeroMatrix()
	{
		final int m = this.getRowDimension(), n = this.getColumnDimension();
		final boolean isZero = true;

		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (this.get(i, j) != 0)
				{
					return false;
				}
			}
		}
		return isZero;
	}

// ######################################################################################
	/**
	 * isZeroRow returns true if the ith row is all zeros
	 * 
	 * @param r
	 *            row to check for full zeros.
	 * @return true if the row is full of zeros.
	 */
	public boolean isZeroRow(final int r)
	{
		PNMatrix A = new PNMatrix(1, this.getColumnDimension());
		A = this.getMatrix(r, r, 0, this.getColumnDimension() - 1);
		return A.isZeroMatrix();
	}

// ######################################################################################
	/**
	 * Add a linear combination of column k to columns in array j[].
	 * 
	 * @param k
	 *            Column index to add.
	 * @param chk
	 *            Coefficient of col to add
	 * @param j
	 *            Array of column indices to add to.
	 * @param jC
	 *            Array of coefficients of column indices to add to.
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void linearlyCombine(final int k, final int chk, final int[] j, final int[] jC)
	{
		// k is column index of coefficient of col to add
		// chj is coefficient of col to add
		int chj = 0; // coefficient of column to add to
		final int m = this.getRowDimension();

		for (int i = 0; i < j.length; i++)
		{
			if (j[i] != 0)
			{
				chj = jC[i];
				// System.out.print("\nchk = " + chk + "\n");
				for (int w = 0; w < m; w++)
				{
					this.set(w, j[i] - 1, chj * this.get(w, k) + chk * this.get(w, j[i] - 1));
				}
			}
		}
	}

// ######################################################################################
	/**
	 * Add a linear combination of column k to columns in array j[].
	 * 
	 * @param k
	 *            Column index to add.
	 * @param alpha
	 *            Array of coefficients of col to add
	 * @param j
	 *            Array of column indices to add to.
	 * @param beta
	 *            Array of coefficients of column indices to add to.
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void linearlyCombine(final int k, final int[] alpha, final int[] j, final int[] beta)
	{
		// k is column index of coefficient of col to add
		// a is array of coefficients of col to add
		// int chk = 0; // coefficient of column to add to
		final int m = this.getRowDimension(), n = j.length;

		for (int i = 0; i < n; i++)
		{
			if (j[i] != 0)
			{
				// chk = jC[i];
				// System.out.print("\nchk = " + chk + "\n");
				for (int w = 0; w < m; w++)
				{
					// for all the elements in a column
					this.set(w, j[i], alpha[i] * this.get(w, k) + beta[i] * this.get(w, j[i]));
				}
			}
		}
	}

// ######################################################################################
	/**
	 * C = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
	public PNMatrix minus(final PNMatrix B)
	{
		this.checkMatrixDimensions(B);
		final int[][] C = new int[this.m][this.n]; // = X.getArray();
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[i][j] = this.A[i][j] - B.A[i][j];
			}
		}
		final PNMatrix X = new PNMatrix(C);
		return X;
	}

// ######################################################################################
	/**
	 * A = A - B
	 * 
	 * @param B
	 *            another matrix
	 * @return A - B
	 */
	public PNMatrix minusEquals(final PNMatrix B)
	{
		this.checkMatrixDimensions(B);
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = this.A[i][j] - B.A[i][j];
			}
		}
		return this;
	}

// ######################################################################################
	/**
	 * Form a matrix with columns the row indices of non-zero elements.
	 * 
	 * @return The matrix with columns the row indices of non-zero elements.
	 *         First row has index 1.
	 */
	public PNMatrix nonZeroIndices()
	{
		final PNMatrix X = new PNMatrix(this.m, this.n);

		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				if (this.get(i, j) == 0)
				{
					X.set(i, j, 0);
				}
				else
				{
					X.set(i, j, i + 1);
				}
			}
		}

		return X;
	}

// ######################################################################################
	/**
	 * C = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
	public PNMatrix plus(final PNMatrix B)
	{
		this.checkMatrixDimensions(B);
		final PNMatrix X = new PNMatrix(this.m, this.n);
		final int[][] C = X.getArray();
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[i][j] = this.A[i][j] + B.A[i][j];
			}
		}
		return X;
	}

// ######################################################################################
	/**
	 * A = A + B
	 * 
	 * @param B
	 *            another matrix
	 * @return A + B
	 */
	public PNMatrix plusEquals(final PNMatrix B)
	{
		this.checkMatrixDimensions(B);
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = this.A[i][j] + B.A[i][j];
			}
		}
		return this;
	}

// ######################################################################################
	/**
	 * Print the matrix to stdout. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */
	public void print(final int w, final int d)
	{
		this.print(new PrintWriter(System.out, true), w, d);
	}

// ######################################################################################
	/**
	 * Print the matrix to stdout. Line the elements up in columns. Use the
	 * format object, and right justify within columns of width characters. Note
	 * that if the matrix is to be read back in, you probably will want to use a
	 * NumberFormat that is set to UK Locale.
	 * 
	 * @param format
	 *            A Formatting object for individual elements.
	 * @param width
	 *            Field width for each column.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */
	public void print(final NumberFormat format, final int width)
	{
		this.print(new PrintWriter(System.out, true), format, width);
	}

// ######################################################################################
	/**
	 * Print the matrix to the output stream. Line the elements up in columns
	 * with a Fortran-like 'Fw.d' style format.
	 * 
	 * @param output
	 *            Output stream.
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 */
	public void print(final PrintWriter output, final int w, final int d)
	{
		final DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.UK));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(d);
		format.setMinimumFractionDigits(d);
		format.setGroupingUsed(false);
		this.print(output, format, w + 2);
	}

	/**
	 * Print the matrix to the output stream. Line the elements up in columns.
	 * Use the format object, and right justify within columns of width
	 * characters. Note that is the matrix is to be read back in, you probably
	 * will want to use a NumberFormat that is set to US Locale.
	 * 
	 * @param output
	 *            the output stream.
	 * @param format
	 *            A formatting object to format the matrix elements
	 * @param width
	 *            Column width.
	 * @see java.text.DecimalFormat#setDecimalFormatSymbols
	 */
	public void print(final PrintWriter output, final NumberFormat format, final int width)
	{
		output.println(); // start on new line.
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				final String s = format.format(this.A[i][j]); // format the
																// number
				final int padding = Math.max(1, width - s.length()); // At
																		// _least_
																		// 1
				// space
				for (int k = 0; k < padding; k++)
				{
					output.print(' ');
				}
				output.print(s);
			}
			output.println();
		}
		output.println(); // end with blank line.
	}

// ######################################################################################
	/**
	 * Print the matrix to a string. Line the elements up in columns with a
	 * Fortran-like 'Fw.d' style format.
	 * 
	 * @param w
	 *            Column width.
	 * @param d
	 *            Number of digits after the decimal.
	 * @return The formated string to output.
	 */
	public String printString(final int w, final int d)
	{
		if (this.isZeroMatrix())
		{
			return "\nNone\n\n";
		}

		final ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();

		this.print(new PrintWriter(arrayStream, true), w, d);

		final String output = arrayStream.toString();

		return output;
	}

// ######################################################################################
	/**
	 * Find the first row with a negative element in a matrix.
	 * 
	 * @return Row index (starting from 0 for 1st row) of the first row from top
	 *         that is has a negative element, -1 of there is no such row.
	 */
	public int rowWithNegativeElement()
	{
		final int m = this.getRowDimension(), n = this.getColumnDimension();
		final int h = -1;

		for (int i = 0; i < m; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (this.get(i, j) < 0)
				{
					return i;
				}
			}
		}
		return h;
	}

// ######################################################################################
	/**
	 * Set a single element.
	 * 
	 * @param i
	 *            Row index.
	 * @param j
	 *            Column index.
	 * @param s
	 *            A(i,j).
	 * @exception ArrayIndexOutOfBoundsException
	 */
	public void set(final int i, final int j, final int s)
	{
		this.A[i][j] = s;
	}

// ######################################################################################
	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(i0:i1,j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public void setMatrix(final int i0, final int i1, final int j0, final int j1, final PNMatrix X)
	{
		try
		{
			for (int i = i0; i <= i1; i++)
			{
				for (int j = j0; j <= j1; j++)
				{
					this.A[i][j] = X.get(i - i0, j - j0);
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	/**
	 * Set a submatrix.
	 * 
	 * @param i0
	 *            Initial row index
	 * @param i1
	 *            Final row index
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public void setMatrix(final int i0, final int i1, final int[] c, final PNMatrix X)
	{
		try
		{
			for (int i = i0; i <= i1; i++)
			{
				for (int j = 0; j < c.length; j++)
				{
					this.A[i][c[j]] = X.get(i - i0, j);
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param j0
	 *            Initial column index
	 * @param j1
	 *            Final column index
	 * @param X
	 *            A(r(:),j0:j1)
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public void setMatrix(final int[] r, final int j0, final int j1, final PNMatrix X)
	{
		try
		{
			for (int i = 0; i < r.length; i++)
			{
				for (int j = j0; j <= j1; j++)
				{
					this.A[r[i]][j] = X.get(i, j - j0);
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	/**
	 * Set a submatrix.
	 * 
	 * @param r
	 *            Array of row indices.
	 * @param c
	 *            Array of column indices.
	 * @param X
	 *            A(r(:),c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public void setMatrix(final int[] r, final int[] c, final PNMatrix X)
	{
		try
		{
			for (int i = 0; i < r.length; i++)
			{
				for (int j = 0; j < c.length; j++)
				{
					this.A[r[i]][c[j]] = X.get(i, j);
				}
			}
		}
		catch (final ArrayIndexOutOfBoundsException e)
		{
			throw new ArrayIndexOutOfBoundsException("Submatrix indices");
		}
	}

// ######################################################################################
	public void setToZero()
	{
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = 0;
			}
		}
	}

// ######################################################################################

// ######################################################################################
	/**
	 * Multiply a matrix by an int in place, A = s*A
	 * 
	 * @param s
	 *            int multiplier
	 * @return replace A by s*A
	 */
	public PNMatrix timesEquals(final int s)
	{
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				this.A[i][j] = s * this.A[i][j];
			}
		}
		return this;
	}

// ######################################################################################
	// DecimalFormat is a little disappointing coming from Fortran or C's
	// printf.
	// Since it doesn't pad on the left, the elements will come out different
	// widths. Consequently, we'll pass the desired column width in as an
	// argument and do the extra padding ourselves.

	// ######################################################################################
	/**
	 * Matrix transpose.
	 * 
	 * @return A'
	 */
	public PNMatrix transpose()
	{
		final PNMatrix X = new PNMatrix(this.n, this.m);
		final int[][] C = X.getArray();
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[j][i] = this.A[i][j];
			}
		}
		return X;
	}

// ######################################################################################
	/**
	 * Unary minus
	 * 
	 * @return - A
	 */
	public PNMatrix uminus()
	{
		final PNMatrix X = new PNMatrix(this.m, this.n);
		final int[][] C = X.getArray();
		for (int i = 0; i < this.m; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				C[i][j] = -this.A[i][j];
			}
		}
		return X;
	}

// ######################################################################################
	/**
	 * Multiply a row matrix by a column matrix, A = s*A
	 * 
	 * @param B
	 *            column vector
	 * @return product of row vector A by column vector B
	 */
	public int vectorTimes(final PNMatrix B)
	{
		int product = 0;

		for (int j = 0; j < this.n; j++)
		{
			product += this.A[0][j] * B.get(j, 0);
		}

		return product;
	}
}
// ######################################################################################
