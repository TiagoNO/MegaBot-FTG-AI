
public class CNSTs {
	/**
	 * The values to define "Far/Middle/Near" distance between the fighter and his opponent. 
	 *        {Game region}
	 * .--------------------------------------,
	 * |        .              .        .     |
	 * |  {FAR} .   {MIDDLE}   . {NEAR} .     |
	 * |        .              .        .     |
	 * |        .              .        .     |
	 * |  O     .              .   O    .     |
	 * |  |     .              .   |    .     |
	 * |  ^     .              .   ^    .     |
	 * `--------------------------------------^
	 *          ^              ^        ^
	 *       FAR_MIN           |        |
	 *     (= MIDDLE_MAX)      |        |
	 *                      MIDDLE_MIN  |
	 *                    (= NEAR_MAX)  |
	 *                               NEAR_MIN
	 */
	public static final int FAR_DIST_MIN = 400; 
	public static final int MIDDLE_DIST_MAX = 400; 
	public static final int MIDDLE_DIST_MIN = 160; 
	public static final int NEAR_DIST_MAX = 160; 
	public static final int NEAR_DIST_MIN = -160; //Maybe, is not needed. Though, adopted to prevent BUG.
	

}
