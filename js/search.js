// When the user clicks on the search box, we want to toggle the search dropdown
function displayToggleSearch(e) {
  e.preventDefault();
  e.stopPropagation();

  closeDropdownSearch(e);
  
  if (idx === null) {
    console.log("Building search index...");
    prepareIdxAndDocMap();
    console.log("Search index built.");
  }
  const dropdown = document.querySelector("#search-dropdown-content");
  if (dropdown) {
    if (!dropdown.classList.contains("show")) {
      dropdown.classList.add("show");
    }
    document.addEventListener("click", closeDropdownSearch);
    document.addEventListener("keydown", searchOnKeyDown);
    document.addEventListener("keyup", searchOnKeyUp);
  }
}

//We want to prepare the index only after clicking the search bar
var idx = null
const docMap = new Map()

function prepareIdxAndDocMap() {
  const docs = [  
    {
      "title": "1. Basics",
      "url": "/ropes/learn-the-ropes/1-basics.html",
      "content": "Section 1 - Basics Let’s dive straight into an example. Twitter Handles We know that (simplistically) a Twitter handle always starts with a literal ‘@’ symbol, followed by any string. We can define that with: import ropes.core._ type TwitterHandle = Concat[Literal['@'], AnyString] Or you can use Concat as an infix type: type TwitterHandle = Literal['@'] Concat AnyString Now, we can parse matching strings, and write them back to their original form: Rope.parseTo[TwitterHandle](\"!Bob\") // res0: Either[Rope.InvalidValue.type, TwitterHandle] = Left( // value = InvalidValue // ) val Right(howy) = Rope.parseTo[TwitterHandle](\"@HowyP\") // howy: TwitterHandle = Concat( // prefix = Literal(value = '@'), // suffix = AnyString(value = \"HowyP\") // ) howy.write // res1: String = \"@HowyP\" Composing and decomposing After parsing, we can access the parts of the rope based on the properties of each individual type. In this case, they are pretty basic. A Concat has a prefix and suffix, and the Literal and AnyString contain single values: howy.prefix // res2: Literal[@] = Literal(value = '@') howy.prefix.value // res3: Char = '@' howy.suffix // res4: AnyString = AnyString(value = \"HowyP\") howy.suffix.value // res5: String = \"HowyP\" We can also create new handles from scratch, or modify existing ones: val twitter: TwitterHandle = Concat(Literal('@'), AnyString(\"Twitter\")) // twitter: TwitterHandle = Concat( // prefix = Literal(value = '@'), // suffix = AnyString(value = \"Twitter\") // ) val bob: TwitterHandle = howy.copy(suffix = AnyString(\"Bob\")) // bob: TwitterHandle = Concat( // prefix = Literal(value = '@'), // suffix = AnyString(value = \"Bob\") // ) Generating Lastly, a feature of all Ropes is that they can be generated via Scalacheck Arbitrary: import org.scalacheck.Arbitrary.arbitrary import ropes.scalacheck._ List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\\n') // res6: List[String] = List( // \"\"\"@⁬꒸㧩첑䬜儍̀艅⸍갳읦㶄灣뜩ෳ쀏垸隰㞥䪺㟠儶㜧⏏㭻ᕦස㷯庑狨အ胡祉䍅⡱魼脳㻴毹嚋䓫ύ↡䪝ೳ㌳๢ᜰ畋㪞Ẵ㕏㋒ // \"\"\", // \"\"\"@츳뚟뀏旹퐷ⴞæÛ㧮趍탒蜖撚眫쬏ﲔ৷랐ￕ㒡뚙ﭷ។銬侀찎셃ꐉ쿄ꛯᖼ⦙嚮鞋甤䦛⧷䦑妺墨 // \"\"\", // \"\"\"@欐퐷뭂륭៶嬹׋콪ᓟು㷄⺒羂⒢鋽ད聩惊蛪畱褅♪떧畟秄겓 // \"\"\", // \"\"\"@蠫࿼‥᪻Ὄጢ靡ꟃ㾾᪉㐏톧峐᝟叉⅂朾蓒픬뫭ヅ☫鰎ㅖ錣븮簐졲楐邡榲භ뗦惴ک▸깇傮䧐Ḥ偫鬦泄읝묋霨넔㸧Ꝍ义籀 // \"\"\", // \"\"\"@銗쑢턥⃃窯蒲ᒴ漮낛馅ꂖր鳃䵁痁㻅腫ຎⲳ뙤괶菼쟙渐鯔闬姼훏腪䢩炼˼ꚑ뽈 // \"\"\" // )"
    } ,    
    {
      "title": "2. Letters",
      "url": "/ropes/learn-the-ropes/2-letters.html",
      "content": "Section 2 - Letters Restricting with Repeated and Letter But wait! The handles we made in section 1 don’t look very realistic. Only 15 letters are allowed for the username portion of the handle. Let’s update our specification: import ropes.core._ type Username = Repeated[1, 15, Letter] type TwitterHandle = Literal['@'] Concat Username Being more precise, we’ve stated that the Username must consist of letter characters, repeated 1 to 15 times. It now will not allow usernames which are too long or have non-letter characters: Rope.parseTo[TwitterHandle](\"@HowyP\") // res0: Either[Rope.InvalidValue.type, TwitterHandle] = Right( // value = Concat( // prefix = Literal(value = '@'), // suffix = Repeated( // values = List( // First(value = CharacterClass(value = 'H')), // Second(value = CharacterClass(value = 'o')), // Second(value = CharacterClass(value = 'w')), // Second(value = CharacterClass(value = 'y')), // First(value = CharacterClass(value = 'P')) // ) // ) // ) // ) Rope.parseTo[TwitterHandle](\"@TwoManyCharactersForAUsername\") // res1: Either[Rope.InvalidValue.type, TwitterHandle] = Left( // value = InvalidValue // ) Rope.parseTo[TwitterHandle](\"@foo&amp;bar\") // res2: Either[Rope.InvalidValue.type, TwitterHandle] = Left( // value = InvalidValue // ) Now, let’s try generating some handles again: import org.scalacheck.Arbitrary.arbitrary import ropes.scalacheck._ List.fill(5)(arbitrary[TwitterHandle].sample).flatten.map(_.write + '\\n') // res3: List[String] = List( // \"\"\"@fpuZklUDFin // \"\"\", // \"\"\"@syWdaCXUJWTPUIu // \"\"\", // \"\"\"@q // \"\"\", // \"\"\"@zMAZLuDJqjXtCrJ // \"\"\", // \"\"\"@fpGJhiuiVNUun // \"\"\" // ) Restricting allowed characters with Range Letter comes pre-defined in ropes as: type Letter = Letter.Uppercase Or Letter.Lowercase object Letter { type Uppercase = Range['A', 'Z'] type Lowercase = Range['a', 'z'] } It defines the upper and lower case characters using a Range, which takes two literal type parameters specifying the minimum and maximum characters allowed. Or lets us to join the two, allowing characters from either range."
    } ,    
    {
      "title": "3. Character Classes",
      "url": "/ropes/learn-the-ropes/3-character-classes.html",
      "content": "Section 3 - Character Classes Looking at the Twitter spec more closely, digits and _ characters are also allowed in the username portion of the handle. A simple Range will not be sufficient here, so we can turn to CharacterClass and it’s associated Spec types. import ropes.core._ import ropes.core.Spec._ type Username = Repeated[1, 15, CharacterClass[('a' - 'z') || ('A' - 'Z') || ('0' - '9') || ==['_']]] type TwitterHandle = Literal['@'] Concat Username To keep things concise, it uses symbolic definitions, so let’s at these in turn: 'x' - 'y' defines that any characters from x up to and including y are allowed ==['x'] defines that only character x is allowed a || b defines that characters matching the spec a or the spec b are allowed"
    } ,    
    {
      "title": "3. Concats",
      "url": "/ropes/learn-the-ropes/4-concats.html",
      "content": "Section 3 - Handing Multiple Concats - Social Security Numbers According to Wikipedia a US Social Security number is: a nine-digit number in the format “AAA-GG-SSSS”. The number is divided into three parts: the first three digits, known as the area number because they were formerly assigned by geographical region; the middle two digits, known as the group number; and the final four digits, known as the serial number. So let’s build that as a Rope: import ropes.core._ type Area = Repeated.Exactly[3, Range['0', '9']] type Group = Repeated.Exactly[2, Range['0', '9']] type Serial = Repeated.Exactly[4, Range['0', '9']] type Dash = Literal['-'] type SSN = Concat[Area, Concat[Dash, Concat[Group, Concat[Dash, Serial]]]] The Repeated.Exactly[N, R] is just an alias for Repeated which uses the same value for maximum and minimum instances. To allow multiple concatenations, the nesting must occur on the suffix rather than the prefix. Using section We could access parts of the SSN in the same way as we have done previously: val Right(parsed) = Rope.parseTo[SSN](\"078-05-1120\") // parsed: SSN = Concat( // prefix = Repeated( // values = List( // CharacterClass(value = '0'), // CharacterClass(value = '7'), // CharacterClass(value = '8') // ) // ), // suffix = Concat( // prefix = Literal(value = '-'), // suffix = Concat( // prefix = Repeated( // values = List(CharacterClass(value = '0'), CharacterClass(value = '5')) // ), // suffix = Concat( // prefix = Literal(value = '-'), // suffix = Repeated( // values = List( // CharacterClass(value = '1'), // CharacterClass(value = '1'), // CharacterClass(value = '2'), // CharacterClass(value = '0') // ) // ) // ) // ) // ) // ) parsed.prefix.write // res0: String = \"078\" parsed.suffix.suffix.prefix.write // res1: String = \"05\" parsed.suffix.suffix.suffix.suffix.write // res2: String = \"1120\" but it is clumsy to navigate through all of the prefixes and suffixes. Instead, we ropes provides the section method to access a given section by index: parsed.section[1].write // res3: String = \"078\" parsed.section[3].write // res4: String = \"05\" parsed.section[5].write // res5: String = \"1120\""
    } ,    
    {
      "title": "4. The Ropes DSL",
      "url": "/ropes/learn-the-ropes/5-dsl.html",
      "content": "Section 4 - The Ropes DSL The definition for SSN we have so far isn’t very easy to read because of all the nesting of Concats. The DSL module provides symbolic operations to make this sort of thing simpler. Using it, we can re-write our definition using the +: syntax from the DSL: import ropes.dsl._ type SSN = Area +: Dash +: Group +: Dash +: Serial"
    } ,    
    {
      "title": "5. Naming",
      "url": "/ropes/learn-the-ropes/6-naming.html",
      "content": "Section 5 - Naming sections with Named If we’d like, we can name parts of our rope: import ropes.core._ type Area = Repeated.Exactly[3, Digit] Named \"Area\" type Group = Repeated.Exactly[2, Digit] Named \"Group\" type Serial = Repeated.Exactly[4, Digit] Named \"Serial\" This makes sections easy to acess: parsed.section[\"Area\"].write // res0: String = \"078\" parsed.section[\"Group\"].write // res1: String = \"05\" parsed.section[\"Serial\"].write // res2: String = \"1120\""
    } ,    
    {
      "title": "6. Conversions",
      "url": "/ropes/learn-the-ropes/7-conversions.html",
      "content": "Section 6 - Conversions When we’ve accessed the area, group and serial numbers in previous sections, we’ve used .write to return them as Strings. That’s because the actual return type is Repeated.Exactly[N, Digit], which gives it’s values as a list of Ints. That’s a bit difficult to work with, and they’d be more naturally represented as Ints. Ropes provides a way to do this using ConvertedTo, which takes a rope type and the type you want to convert to and from: import ropes.core._ type Area = Repeated.Exactly[3, Digit] ConvertedTo Int Named \"Area\" type Group = Repeated.Exactly[2, Digit] ConvertedTo Int Named \"Group\" type Serial = Repeated.Exactly[4, Digit] ConvertedTo Int Named \"Serial\" We can now use .value on each section and get a simple Int: parsed.section[\"Area\"].value // res0: Int = 78 parsed.section[\"Group\"].value // res1: Int = 5 parsed.section[\"Serial\"].value // res2: Int = 1120"
    } ,          
  ];

  idx = lunr(function () {
    this.ref("title");
    this.field("content");

    docs.forEach(function (doc) {
      this.add(doc);
    }, this);
  });

  docs.forEach(function (doc) {
    docMap.set(doc.title, doc.url);
  });
}

// The onkeypress handler for search functionality
function searchOnKeyDown(e) {
  const keyCode = e.keyCode;
  const parent = e.target.parentElement;
  const isSearchBar = e.target.id === "search-bar";
  const isSearchResult = parent ? parent.id.startsWith("result-") : false;
  const isSearchBarOrResult = isSearchBar || isSearchResult;

  if (keyCode === 40 && isSearchBarOrResult) {
    // On 'down', try to navigate down the search results
    e.preventDefault();
    e.stopPropagation();
    selectDown(e);
  } else if (keyCode === 38 && isSearchBarOrResult) {
    // On 'up', try to navigate up the search results
    e.preventDefault();
    e.stopPropagation();
    selectUp(e);
  } else if (keyCode === 27 && isSearchBarOrResult) {
    // On 'ESC', close the search dropdown
    e.preventDefault();
    e.stopPropagation();
    closeDropdownSearch(e);
  }
}

// Search is only done on key-up so that the search terms are properly propagated
function searchOnKeyUp(e) {
  // Filter out up, down, esc keys
  const keyCode = e.keyCode;
  const cannotBe = [40, 38, 27];
  const isSearchBar = e.target.id === "search-bar";
  const keyIsNotWrong = !cannotBe.includes(keyCode);
  if (isSearchBar && keyIsNotWrong) {
    // Try to run a search
    runSearch(e);
  }
}

// Move the cursor up the search list
function selectUp(e) {
  if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index) && (index > 0)) {
      const nextIndexStr = "result-" + (index - 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Move the cursor down the search list
function selectDown(e) {
  if (e.target.id === "search-bar") {
    const firstResult = document.querySelector("li[id$='result-0']");
    if (firstResult) {
      firstResult.firstChild.focus();
    }
  } else if (e.target.parentElement.id.startsWith("result-")) {
    const index = parseInt(e.target.parentElement.id.substring(7));
    if (!isNaN(index)) {
      const nextIndexStr = "result-" + (index + 1);
      const querySel = "li[id$='" + nextIndexStr + "'";
      const nextResult = document.querySelector(querySel);
      if (nextResult) {
        nextResult.firstChild.focus();
      }
    }
  }
}

// Search for whatever the user has typed so far
function runSearch(e) {
  if (e.target.value === "") {
    // On empty string, remove all search results
    // Otherwise this may show all results as everything is a "match"
    applySearchResults([]);
  } else {
    const tokens = e.target.value.split(" ");
    const moddedTokens = tokens.map(function (token) {
      // "*" + token + "*"
      return token;
    })
    const searchTerm = moddedTokens.join(" ");
    const searchResults = idx.search(searchTerm);
    const mapResults = searchResults.map(function (result) {
      const resultUrl = docMap.get(result.ref);
      return { name: result.ref, url: resultUrl };
    })

    applySearchResults(mapResults);
  }

}

// After a search, modify the search dropdown to contain the search results
function applySearchResults(results) {
  const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
  if (dropdown) {
    //Remove each child
    while (dropdown.firstChild) {
      dropdown.removeChild(dropdown.firstChild);
    }

    //Add each result as an element in the list
    results.forEach(function (result, i) {
      const elem = document.createElement("li");
      elem.setAttribute("class", "dropdown-item");
      elem.setAttribute("id", "result-" + i);

      const elemLink = document.createElement("a");
      elemLink.setAttribute("title", result.name);
      elemLink.setAttribute("href", result.url);
      elemLink.setAttribute("class", "dropdown-item-link");

      const elemLinkText = document.createElement("span");
      elemLinkText.setAttribute("class", "dropdown-item-link-text");
      elemLinkText.innerHTML = result.name;

      elemLink.appendChild(elemLinkText);
      elem.appendChild(elemLink);
      dropdown.appendChild(elem);
    });
  }
}

// Close the dropdown if the user clicks (only) outside of it
function closeDropdownSearch(e) {
  // Check if where we're clicking is the search dropdown
  if (e.target.id !== "search-bar") {
    const dropdown = document.querySelector("div[id$='search-dropdown'] > .dropdown-content.show");
    if (dropdown) {
      dropdown.classList.remove("show");
      document.documentElement.removeEventListener("click", closeDropdownSearch);
    }
  }
}
