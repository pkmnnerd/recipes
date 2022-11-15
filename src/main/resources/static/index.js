function init() {
  document.getElementById('search-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const term = document.getElementById('search-box').value;
    onSearch(term);
  })

  document.getElementById('saved-recipes-header').addEventListener('click', (e) => {
    toggleSavedRecipes();
  })

  document.getElementById('shopping-list-button').addEventListener('click', (e) => {
    onGenerateShoppingList();
  })

  const recipeItemTemplate = document.getElementById('recipe-list-item-template')
  const savedRecipeItemTemplate = document.getElementById('saved-recipe-item-template')
  const recipeCardTemplate = document.getElementById('recipe-card-template')

  window.customElements.define('recipe-item', class extends HTMLElement {
    constructor() {
      super();
    }
    connectedCallback() {
      this.appendChild(recipeItemTemplate.content.cloneNode(true));
      this.querySelector('button').addEventListener('click', () => {
        saveRecipe(this.getAttribute('data-name'), this.getAttribute('data-id'));
      })
      this.querySelector('span').innerText = this.getAttribute('data-name');
    }
  });

  window.customElements.define('recipe-card', class extends HTMLElement {
    constructor() {
      super();
    }
    connectedCallback() {
      this.appendChild(recipeCardTemplate.content.cloneNode(true));
      this.querySelector('save-button').addEventListener('click', () => {
        saveRecipe(this.getAttribute('data-name'), this.getAttribute('data-id'));
      })
      this.querySelector('.card-title').innerText = this.getAttribute('data-name');
    }
  });

  window.customElements.define('saved-recipe-item', class extends HTMLElement {
    constructor() {
      super();
    }
    connectedCallback() {
      this.appendChild(savedRecipeItemTemplate.content.cloneNode(true));
      this.querySelector('button').addEventListener('click', () => {
        removeRecipe(this.getAttribute('data-id'), this);
      })
      this.querySelector('span').innerText = this.getAttribute('data-name');
    }
  });
}

function toggleSavedRecipes() {
  const icon = document.getElementById('saved-recipes-expand-button');
  const savedRecipesContainer = document.getElementById('saved-recipes');
  console.log(savedRecipesContainer.classList);
  if (savedRecipesContainer.style.height === '0px') {
    icon.classList.replace('bi-chevron-up', 'bi-chevron-down');
    savedRecipesContainer.style.height = 'auto';
  } else {
    icon.classList.replace('bi-chevron-down', 'bi-chevron-up');
    savedRecipesContainer.style.height = '0px';
  }
}

function saveRecipe(name, id) {
  if (savedRecipes.has(id)) {
    return;
  }
  savedRecipes.set(id, name);

  const savedRecipeItem = document.createElement('saved-recipe-item');
  savedRecipeItem.setAttribute('data-name', name);
  savedRecipeItem.setAttribute('data-id', id);

  const savedRecipesListElement = document.getElementById('saved-recipes-list');
  savedRecipesListElement.appendChild(savedRecipeItem);


  if (savedRecipes.size === 1) {
    const noRecipesText = document.getElementById('saved-recipes').querySelector('p');
    noRecipesText.classList.add('d-none');
    const shoppingListButton = document.getElementById('shopping-list-button');
    shoppingListButton.classList.remove('d-none');
    savedRecipesListElement.classList.remove('d-none');
  }

  const savedRecipesHeader = document.getElementById('saved-recipes-header-text');
  savedRecipesHeader.innerText = `Recipes (${savedRecipes.size})`;
}

function removeRecipe(id, element) {
  element.remove()
  savedRecipes.delete(id);

  const savedRecipesHeader = document.getElementById('saved-recipes-header-text');
  savedRecipesHeader.innerText = `Recipes (${savedRecipes.size})`;
  
  if (savedRecipes.size === 0) {
    const shoppingListButton = document.getElementById('shopping-list-button');
    shoppingListButton.classList.add('d-none');
    const savedRecipesListElement = document.getElementById('saved-recipes-list');
    savedRecipesListElement.classList.add('d-none');
    noRecipesText.classList.remove('d-none');
  }
}


const savedRecipes = new Map();

window.addEventListener('DOMContentLoaded', init, false);

async function onSearch(term) {
  const response = await fetch(`/recipes/api/recipes?term=${term}`);
  if (response.ok) {
    const results = await response.json();
    const recipesList = document.getElementById('recipes');
    recipesList.replaceChildren();
    results.forEach((recipe) => {

      const recipeCard = document.getElementById('recipe-card-template').content.cloneNode(true);
      recipeCard.querySelector('.card-title').innerText = recipe.name;
      recipeCard.querySelector('.card-text').innerText = `${recipe.category} - ${recipe.region}`;
      recipeCard.querySelector('.save-button').addEventListener('click', () => {
        saveRecipe(recipe.name, recipe.id);
      })
      recipeCard.querySelector('.view-recipe-button').href=`https://www.themealdb.com/meal.php?c=${recipe.id}`;
      recipeCard.querySelector('img').src = recipe.imageLink;
      recipeCard.querySelector('img').alt = recipe.name;
      recipesList.appendChild(recipeCard);
      /*
      const recipeItem = document.createElement('recipe-card');
      recipeItem.setAttribute('data-name', recipe.name);
      recipeItem.setAttribute('data-id', recipe.id);
      recipesList.appendChild(recipeItem);
      */
    });
  } else {
    const recipesContainer = document.getElementById('recipes');
    recipesContainer.innerHTML = `<div class="alert alert-danger" role="alert">${response.status}: ${response.statusText}</div>`;

  }
}

async function onGenerateShoppingList() {
  const modalContent = document.getElementById('shopping-list')
  modalContent.replaceChildren();
  const shoppingListModal = new bootstrap.Modal(document.getElementById('shopping-list-modal'));
  shoppingListModal.show()

  const recipeIds = Array.from(savedRecipes.keys()).join(',');
  const response = await fetch(`/recipes/api/shoppinglist?recipeIds=${recipeIds}`);

  if (response.ok) {
    const results = await response.json();
    const shoppingList = [...results.shoppingList];
    shoppingList.sort((a, b) => a.ingredientName.localeCompare(b.ingredientName));
    const listElement = document.createElement('ul');
    shoppingList.forEach(({ ingredientName, quantities }) => {
      const shoppingListItem = document.createElement('li');
      shoppingListItem.classList.add('mb-1');
      shoppingListItem.innerText = `${ingredientName} - ${quantities.join(' + ')}`;
      listElement.appendChild(shoppingListItem);
    });
    modalContent.appendChild(listElement);

  } else {
    modalContent.innerHTML = `<div class="alert alert-danger" role="alert">${response.status}: ${response.statusText}</div>`;
  }
}
