package eu.h2020.helios_social.modules.contentawareprofiling.interestcategories;

import java.util.ArrayList;
import java.util.List;

/**
 * This class documents the interest categories hierarchy and specifically defines the 15 coarse
 * categories, 4 of which are compound and further analysed in subcategories.
 */
public class InterestCategoriesHierarchy {
    /**
     * The coarse interest categories
     */
    public static ArrayList<InterestCategories> coarseCategories = new ArrayList<InterestCategories>(){
        {
            add(InterestCategories.animals);
            add(InterestCategories.architecture);
            add(InterestCategories.art);
            add(InterestCategories.entertainment);
            add(InterestCategories.fashion);
            add(InterestCategories.food_drinks);
            add(InterestCategories.health_fitness);
            add(InterestCategories.home_decor);
            add(InterestCategories.kids_babies);
            add(InterestCategories.plants_flowers);
            add(InterestCategories.sports);
            add(InterestCategories.tattoos);
            add(InterestCategories.technology);
            add(InterestCategories.travel);
            add(InterestCategories.vehicles);
        }
    };

    /**
     * The compound interest categories
     */
    public enum CompoundCategories {
        // it is important that these are in the same order that they appear in the coarse profile
        entertainment {
            @Override
            public List<InterestCategories> getSubcategories() {
                return InterestCategoriesHierarchy.entertainmentSubcat;
            }

            @Override
            public int getIndex() {
                return InterestCategoriesHierarchy.coarseCategories.indexOf(InterestCategories.entertainment);
            }
        },
        fashion {
            @Override
            public List<InterestCategories> getSubcategories() {
                return InterestCategoriesHierarchy.fashionSubcat;
            }

            @Override
            public int getIndex() {
                return InterestCategoriesHierarchy.coarseCategories.indexOf(InterestCategories.fashion);
            }
        },
        sports {
            @Override
            public List<InterestCategories> getSubcategories() {
                return InterestCategoriesHierarchy.sportsSubcat;
            }

            @Override
            public int getIndex() {
                return InterestCategoriesHierarchy.coarseCategories.indexOf(InterestCategories.sports);
            }
        },
        vehicles {
            @Override
            public List<InterestCategories> getSubcategories() {
                return InterestCategoriesHierarchy.vehiclesSubcat;
            }

            @Override
            public int getIndex() {
                return InterestCategoriesHierarchy.coarseCategories.indexOf(InterestCategories.vehicles);
            }
        };

        public abstract List<InterestCategories> getSubcategories();
        public abstract int getIndex();
    }

    /**
     * The subcategories of the entertainment interest category.
     */
    public static List<InterestCategories> entertainmentSubcat = new ArrayList<InterestCategories>() {
        {
            add(InterestCategories.books);
            add(InterestCategories.general_entertainment);
            add(InterestCategories.movies_series_anime);
            add(InterestCategories.music);
            add(InterestCategories.video_games);
        }
    };

    /**
     * The subcategories of the fashion interest category.
     */
    public static List<InterestCategories> fashionSubcat = new ArrayList<InterestCategories>() {
        {
            add(InterestCategories.bags);
            add(InterestCategories.clothes);
            add(InterestCategories.hair);
            add(InterestCategories.jewellery);
            add(InterestCategories.makeup_beauty);
            add(InterestCategories.nails);
            add(InterestCategories.shoes);
        }
    };

    /**
     * The subcategories of the sports interest category.
     */
    public static List<InterestCategories> sportsSubcat = new ArrayList<InterestCategories>() {
        {
            add(InterestCategories.RockClimbing);
            add(InterestCategories.badminton);
            add(InterestCategories.basketball);
            add(InterestCategories.bocce);
            add(InterestCategories.croquet);
            add(InterestCategories.football);
            add(InterestCategories.gymnastics);
            add(InterestCategories.polo);
            add(InterestCategories.rowing);
            add(InterestCategories.sailing);
            add(InterestCategories.snowboarding);
            add(InterestCategories.soccer);
            add(InterestCategories.swimming);
            add(InterestCategories.tennis);
        }
    };

    /**
     * The subcategories of the vehicles interest category.
     */
    public static List<InterestCategories> vehiclesSubcat = new ArrayList<InterestCategories>() {
        {
            add(InterestCategories.boats);
            add(InterestCategories.cars);
            add(InterestCategories.motorcycles);
            add(InterestCategories.planes);
            add(InterestCategories.truck);
        }
    };


    /**
     * The expanded list of the interest categories with the compound ones being substituted with their
     * subcategories.
     */
    public static ArrayList<InterestCategories> fineCategories = new ArrayList<InterestCategories>() {
        {
            add(InterestCategories.animals);
            add(InterestCategories.architecture);
            add(InterestCategories.art);
            add(InterestCategories.books);
            add(InterestCategories.general_entertainment);
            add(InterestCategories.movies_series_anime);
            add(InterestCategories.music);
            add(InterestCategories.video_games);
            add(InterestCategories.bags);
            add(InterestCategories.clothes);
            add(InterestCategories.hair);
            add(InterestCategories.jewellery);
            add(InterestCategories.makeup_beauty);
            add(InterestCategories.nails);
            add(InterestCategories.shoes);
            add(InterestCategories.food_drinks);
            add(InterestCategories.health_fitness);
            add(InterestCategories.home_decor);
            add(InterestCategories.kids_babies);
            add(InterestCategories.plants_flowers);
            add(InterestCategories.RockClimbing);
            add(InterestCategories.badminton);
            add(InterestCategories.basketball);
            add(InterestCategories.bocce);
            add(InterestCategories.croquet);
            add(InterestCategories.football);
            add(InterestCategories.gymnastics);
            add(InterestCategories.polo);
            add(InterestCategories.rowing);
            add(InterestCategories.sailing);
            add(InterestCategories.snowboarding);
            add(InterestCategories.soccer);
            add(InterestCategories.swimming);
            add(InterestCategories.tennis);
            add(InterestCategories.tattoos);
            add(InterestCategories.technology);
            add(InterestCategories.travel);
            add(InterestCategories.boats);
            add(InterestCategories.cars);
            add(InterestCategories.motorcycles);
            add(InterestCategories.planes);
            add(InterestCategories.truck);
        }
    };
}
