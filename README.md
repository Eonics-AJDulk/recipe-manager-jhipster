# Recipe Search API

The `recipemanager` API offers a powerful search feature that allows users to find recipes based on specific criteria. The search functionality is accessible through the `/api/recipes/search` endpoint.

The API is built using JHipster. This gives us a lot of functionality out of the box, including a Swagger UI for testing the API. The Swagger UI is available at `http://localhost:8080/swagger-ui/`.

## Search Criteria

The search endpoint accepts a JSON object with the following properties:

1. **Vegetarian Recipes**

   - `vegatarian`: Filter recipes based on whether they are vegetarian. Use `true` or `false` to specify or leave it empty for no filter.
     (Note: The property name is misspelled in the API, but it is kept as is for consistency with the original API.)

2. **Number of Servings**

   - `nrOfServings`: Filter recipes based on the number of servings. Provide an integer value or leave it empty for no filter.

3. **Included Ingredients**

   - `ingredientsToInclude`: Include recipes that contain specific ingredients. Provide an array of ingredient names or leave it empty for no filter.

4. **Excluded Ingredients**

   - `ingredientsToExclude`: Exclude recipes that contain specific ingredients. Provide an array of ingredient names or leave it empty for no filter.

5. **Text Search in Instructions**
   - `instructionSearch`: Filter recipes based on text search within the instructions. Provide a string or leave it empty for no filter.

**Note:**

- To leave a normal field empty, use `null` or `""`.
- To leave array fields, use `null` or specify an empty array (`[]`).

## Example Request

```json
{
  "vegatarian": true,
  "nrOfServings": 4,
  "ingredientsToInclude": ["potatoes", "carrots"],
  "ingredientsToExclude": ["salmon"],
  "instructionSearch": "oven"
}
```

### Example Response

```json
[
  {
    "id": 1,
    "name": "Roasted Vegetables",
    "vegatarian": true,
    "nrOfServings": 4,
    "instructions": "Preheat the oven to 400°F and...",
    "ingredients": [
      {
        "id": 101,
        "name": "potatoes"
      },
      {
        "id": 102,
        "name": "carrots"
      }
    ]
  }
]
```
