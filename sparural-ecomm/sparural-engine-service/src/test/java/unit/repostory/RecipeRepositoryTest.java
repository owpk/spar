package unit.repostory;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sparural.engine.EngineApplication;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.repositories.impl.GoodsRepositoryImpl;
import ru.sparural.engine.repositories.impl.RecipeAttributesRepositoryImpl;
import ru.sparural.engine.repositories.impl.RecipeRepositoryImpl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EngineApplication.class)
public class RecipeRepositoryTest {
    @Autowired
    RecipeRepositoryImpl recipeRepository;

    @Autowired
    RecipeAttributesRepositoryImpl attributeRepository;

    @Autowired
    GoodsRepositoryImpl goodsRepository;

    String uuid = UUID.randomUUID().toString();

    Long recipeId;

    List<RecipeEntity> addedRecipes = new ArrayList<>();

    RecipeEntity recipe;

    List<GoodsEntity> goods = new ArrayList<>();
    List<RecipeAttributeEntity> attributes = new ArrayList<>();

    List<Long> goodsIds = new ArrayList<>();

    List<Long> attributesIds = new ArrayList<>();

    @BeforeEach
    public void initEach() {
        for(int i = 0; i < 5; i++) {
            var entity = new GoodsEntity();
            entity.setName("test " + i);
            entity.setDraft(false);
            entity.setDescription("Desc test " + i);
            entity.setExtGoodsId("uuid " + i);

            goods.add(entity);
        }

        for(int i = 0; i < 3; i++) {
            var entity = new RecipeAttributeEntity();
            entity.setDraft(false);
            entity.setName("test " + i);
            entity.setShowOnPreview(false);

            attributes.add(entity);
        }

        recipe = new RecipeEntity();
        recipe.setDescription("test description");
        recipe.setDraft(false);
        recipe.setFats(0);
        recipe.setCalories(0);
        recipe.setProteins(0);
        recipe.setTitle("test");

//        recipeId = recipeRepository.create(recipe).orElseThrow().getId();
        attributesIds.addAll(addAttributesIntoDB(attributes));
        goodsIds.addAll(addGoodsIntoDB(goods));
    }

    private List<Long> addAttributesIntoDB(List<RecipeAttributeEntity> attributes) {
        List<Long> addedAttributesIds = new ArrayList<>();

        for(RecipeAttributeEntity attribute : attributes) {
            RecipeAttributeEntity addedAttribute = attributeRepository.create(attribute).orElseThrow();
            addedAttributesIds.add(addedAttribute.getId());
        }

        return addedAttributesIds;
    }

    private List<Long> addGoodsIntoDB(List<GoodsEntity> goods) {
        List<Long> addedGoodsIds = new ArrayList<>();

        for(GoodsEntity good : goods) {
            GoodsEntity goodAttribute = goodsRepository.create(good).orElseThrow();
            addedGoodsIds.add(goodAttribute.getId());
        }

        return addedGoodsIds;
    }

    @AfterEach
    public void exit() {
        deleteAll();
    }

    private void deleteAll() {
        for(Long goodId : goodsIds) {
            goodsRepository.delete(goodId);
        }

        for(Long attributeId : attributesIds) {
            attributeRepository.delete(attributeId);
        }

        for(var recipe : addedRecipes) {
            recipeRepository.delete(recipe.getId());
        }

        recipeRepository.delete(recipeId);
    }

    @DisplayName("Add attributes and goods")
    @Test
    void addWithAttributesAndGoods() {


        RecipeEntity recipeEntity = recipeRepository.create(recipe, goodsIds, attributesIds);

        recipeId = recipeEntity.getId();

        RecipeFullEntity recipeFullEntity = recipeRepository.fetchById(recipeId);
        List<Long> fetchedGoodsIds =
                recipeFullEntity.getGoods().stream().map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> fetchedAttrsIds =
                recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(fetchedGoodsIds).containsAll(goodsIds);
        assertThat(fetchedAttrsIds).containsAll(attributesIds);
    }

    @DisplayName("Add + update attributes and goods")
    @Test
    void updateWithAttributesAndGoods() {
        List<Long> innerGoodsIds = List.of(goodsIds.get(1), goodsIds.get(2));
        List<Long> innerAttributesIds = List.of(attributesIds.get(0), attributesIds.get(2));

        RecipeEntity recipeEntity = recipeRepository.create(recipe, innerGoodsIds, innerAttributesIds);
        recipeId = recipeEntity.getId();

        RecipeFullEntity recipeFullEntity = recipeRepository.fetchById(recipeId);
        List<Long> fetchedGoodsIds =
                recipeFullEntity.getGoods().stream().map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> fetchedAttrsIds =
                recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(fetchedGoodsIds).containsAll(innerGoodsIds);
        assertThat(fetchedAttrsIds).containsAll(innerAttributesIds);


        recipeRepository.update(recipeId, recipe, goodsIds, attributesIds);


        RecipeFullEntity updRecipeFullEntity = recipeRepository.fetchById(recipeId);

        List<Long> updFetchedGoodsIds =
                updRecipeFullEntity.getGoods().stream()
                        .map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> updFetchedAttrsIds =
                updRecipeFullEntity.getAttributes().stream()
                        .map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(updFetchedGoodsIds).containsAll(goodsIds);
        assertThat(updFetchedAttrsIds).containsAll(attributesIds);
    }

    @DisplayName("Add + delete attributes and goods")
    @Test
    void deleteWithAttributesAndGoods() {
        List<Long> innerGoodsIds = List.of(goodsIds.get(1), goodsIds.get(2));
        List<Long> innerAttributesIds = List.of(attributesIds.get(0), attributesIds.get(2));

        RecipeEntity recipeEntity = recipeRepository.create(recipe, goodsIds, attributesIds);

        recipeId = recipeEntity.getId();

        RecipeFullEntity recipeFullEntity = recipeRepository.fetchById(recipeId);
        List<Long> fetchedGoodsIds =
                recipeFullEntity.getGoods().stream().map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> fetchedAttrsIds =
                recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(fetchedGoodsIds).containsAll(goodsIds);
        assertThat(fetchedAttrsIds).containsAll(attributesIds);

        recipeRepository.update(recipeId, recipe, innerGoodsIds, innerAttributesIds);


        RecipeFullEntity updRecipeFullEntity = recipeRepository.fetchById(recipeId);

        List<Long> updFetchedGoodsIds =
                updRecipeFullEntity.getGoods().stream()
                        .map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> updFetchedAttrsIds =
                updRecipeFullEntity.getAttributes().stream()
                        .map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(updFetchedGoodsIds.containsAll(innerGoodsIds)).isTrue();
        assertThat(updFetchedAttrsIds.containsAll(innerAttributesIds)).isTrue();
    }

    @DisplayName("Add + update + delete attributes and goods")
    @Test
    void deleteAndUpdateWithAttributesAndGoods() {
        List<Long> goodsForUpdate = List.of(goodsIds.get(3), goodsIds.get(4));
        List<Long> attributesForUpdate = List.of(attributesIds.get(0), attributesIds.get(2));

        List<Long> goodsForDelete = List.of(goodsIds.get(0), goodsIds.get(1), goodsIds.get(2));
        List<Long> attributesForDelete = List.of(attributesIds.get(1), attributesIds.get(0), attributesIds.get(2));

        RecipeEntity recipeEntity = recipeRepository.create(recipe, goodsForDelete, attributesForDelete);

        recipeId = recipeEntity.getId();

        RecipeFullEntity recipeFullEntity = recipeRepository.fetchById(recipeId);
        List<Long> fetchedGoodsIds =
                recipeFullEntity.getGoods().stream().map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> fetchedAttrsIds =
                recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(fetchedGoodsIds).containsAll(goodsForDelete);
        assertThat(fetchedGoodsIds.size()).isEqualTo(goodsForDelete.size());
        assertThat(fetchedAttrsIds).containsAll(attributesForDelete);
        assertThat(fetchedAttrsIds.size()).isEqualTo(attributesForDelete.size());

        recipeRepository.update(recipeId, recipe, goodsForUpdate, attributesForUpdate);


        RecipeFullEntity updRecipeFullEntity = recipeRepository.fetchById(recipeId);

        List<Long> updFetchedGoodsIds =
                updRecipeFullEntity.getGoods().stream()
                        .map(GoodsEntity::getId).collect(Collectors.toList());

        List<Long> updFetchedAttrsIds =
                updRecipeFullEntity.getAttributes().stream()
                        .map(RecipeAttributeEntity::getId).collect(Collectors.toList());

        assertThat(updFetchedGoodsIds.containsAll(goodsForUpdate)).isTrue();
        assertThat(updFetchedAttrsIds.containsAll(attributesForUpdate)).isTrue();
    }

    @DisplayName("Get recipe by id")
    @Test
    void getRecipeById() {
        RecipeEntity recipeEntity = recipeRepository.create(recipe).orElseThrow();
        recipeId = recipeEntity.getId();

        recipeRepository.insertGoodsIntoRecipeById(recipeId, goodsIds);
        recipeRepository.insertAttributesIntoRecipeById(recipeId, attributesIds);

        for(int i = 0; i < 5; i++) {
            List<Long> goods = goodsRepository.getAllRecipeGoodIdByRecipeId(recipeId);
            List<Long> attributes = attributeRepository.getAllRecipeAttributeIdByRecipeId(recipeId);

            assertThat(goods.size()).isEqualTo(goodsIds.size());
            assertThat(attributes.size()).isEqualTo(attributesIds.size());

            RecipeFullEntity recipeFullEntity = recipeRepository.fetchById(recipeId);

            assertThat(recipeFullEntity.getGoods().size()).isEqualTo(goods.size());
            assertThat(recipeFullEntity.getAttributes().size()).isEqualTo(attributesIds.size());
        }
    }

    @DisplayName("Test recipe get")
    @Test
    void getRecipes() {
        recipeRepository.delete(recipeId);
        int offset = 11;

        addedRecipes = addTestRecipes(offset);

        for(int i = 0; i < 5; i++) {
            List<RecipeFullEntity> recipes = recipeRepository.list(0, offset);

            for(var recipe : recipes) {
                List<Long> goods = goodsRepository.getAllRecipeGoodIdByRecipeId(recipe.getId());
                List<Long> attributes = attributeRepository.getAllRecipeAttributeIdByRecipeId(recipe.getId());

                assertThat(goods.size()).isEqualTo(goodsIds.size());
                assertThat(attributes.size()).isEqualTo(attributesIds.size());

                assertThat(recipe.getGoods().size()).isEqualTo(goods.size());
                assertThat(recipe.getAttributes().size()).isEqualTo(attributesIds.size());
            }
        }
    }

    @DisplayName("Check recipe DTO")
    @Test
    void checkDTOS() {
        recipeRepository.delete(recipeId);
        int offset = 11;

        addedRecipes = addTestRecipes(offset);

        Map<Long, RecipeEntity> compare = addedRecipes.stream()
                .collect(Collectors.toMap(RecipeEntity::getId, Function.identity()));

        for(int i = 0; i < 5; i++) {
            List<RecipeFullEntity> fetchedRecipes = recipeRepository.list(0, offset);

            for(int j = 0; j < offset; j++) {
                RecipeFullEntity recipeFullEntity = fetchedRecipes.get(j);
                long id = recipeFullEntity.getId();

                assertThat(recipeFullEntity.getCalories()).isEqualTo(compare.get(id).getCalories());
                assertThat(recipeFullEntity.getDraft()).isEqualTo(compare.get(id).getDraft());
                assertThat(recipeFullEntity.getDescription()).isEqualTo(compare.get(id).getDescription());
                assertThat(recipeFullEntity.getFats()).isEqualTo(compare.get(id).getFats());
                assertThat(recipeFullEntity.getProteins()).isEqualTo(compare.get(id).getProteins());
                assertThat(recipeFullEntity.getTitle()).isEqualTo(compare.get(id).getTitle());
                assertThat(recipeFullEntity.getCarbohydrates()).isEqualTo(compare.get(id).getCarbohydrates());

                assertThat(recipeFullEntity.getGoods().stream().map(GoodsEntity::getExtGoodsId))
                        .containsAll(goods.stream().map(GoodsEntity::getExtGoodsId).collect(Collectors.toList()));
                assertThat(recipeFullEntity.getGoods().stream().map(GoodsEntity::getDescription))
                        .containsAll(goods.stream().map(GoodsEntity::getDescription).collect(Collectors.toList()));
                assertThat(recipeFullEntity.getGoods().stream().map(GoodsEntity::getName))
                        .containsAll(goods.stream().map(GoodsEntity::getName).collect(Collectors.toList()));
                assertThat(recipeFullEntity.getGoods().stream().map(GoodsEntity::getDraft))
                        .containsAll(goods.stream().map(GoodsEntity::getDraft).collect(Collectors.toList()));

                assertThat(recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getShowOnPreview))
                        .containsAll(attributes.stream().map(RecipeAttributeEntity::getShowOnPreview).collect(Collectors.toList()));
                assertThat(recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getName))
                        .containsAll(attributes.stream().map(RecipeAttributeEntity::getName).collect(Collectors.toList()));
                assertThat(recipeFullEntity.getAttributes().stream().map(RecipeAttributeEntity::getDraft))
                        .containsAll(attributes.stream().map(RecipeAttributeEntity::getDraft).collect(Collectors.toList()));
            }
        }
    }

    private List<RecipeEntity> addTestRecipes(int count) {
        List<RecipeEntity> result = new ArrayList<>();

        for(int i = 0; i < count; i++) {
            var recipe = new RecipeEntity();
            recipe.setDescription(uuid);
            recipe.setDraft(getRandomBoolean());
            recipe.setFats(randInt(1, 1000));
            recipe.setCalories(randInt(1, 1000));
            recipe.setProteins(randInt(1, 1000));
            recipe.setTitle("test" + randInt(1, 1000));

            result.add(recipeRepository.create(recipe, goodsIds, attributesIds));
        }

        return result;
    }

    public int randInt(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    private boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
