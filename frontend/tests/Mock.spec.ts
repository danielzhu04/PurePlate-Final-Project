import { test, expect } from "@playwright/test";
import {
  mockedRecommendationData,
  mockedErrorMessage,
} from "../src/pages/software-components/MockedResults";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/software");
  await page.route("http://localhost:3233/pureplate*", (route) => {
    // Check if the request includes specific query params
    const query = route.request().url().split("?")[1];
    if (query.includes("weight=100") && query.includes("height=100")) {
      // Provide a mock response
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(mockedRecommendationData),
      });
    } else {
      // Optionally, let other requests pass through
      route.continue();
    }
  });
});

test("Testing the basic elements of the software page", async ({ page }) => {
  await expect(page.getByRole('heading', { name: 'Weight (kg)' })).toBeVisible
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Gender" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Activity Level" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Only Search Growable Foods?" })).toBeVisible;
  await expect(page.getByText("Select Foods")).toBeVisible;
});

test("Checking if software page works with basic input (100 for demographic information)", async ({
  page,
}) => {

  // Filling out weight 
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
  await page.locator("#txtbx3").click();
  await page.locator("#txtbx3").fill("100");

  // Filling out height
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await page.locator("#txtbx2").click();
  await page.locator("#txtbx2").fill("100");

  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await page.locator("#txtbx1").click();
  await page.locator("#txtbx1").fill("100");

  await page.getByLabel("Male").check();
  await page.getByLabel("Lightly Active").check();
  await page.getByLabel("No").check();
  
  await page.locator("#foods-autocomplete").click();
  await page.getByRole("option", { name: "Carrots, mature, raw" }).click();
  await page
    .getByRole("option", { name: "Restaurant, Latino, pupusas" })
    .click();
  await page.getByLabel("Submit Button").click();
  await page.getByRole("cell", { name: "Salt, table, iodized" }).click();
  await page.getByRole("cell", { name: "Flour, soy, defatted" }).click();
});

test("checking that an alert happens when we dont have the correct number of imputs", async ({
  page,
}) => {

  // Filling out weight 
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
  await page.locator("#txtbx3").click();
  await page.locator("#txtbx3").fill("h");

  page.on("dialog", async (dialog) => {
    // Assert the message in the alert dialog
    expect(dialog.message()).toBe("Value Entered is not a valid number");

    // You can dismiss the dialog if necessary
    await dialog.dismiss();
  });
  // Filling out height
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await page.locator("#txtbx2").click();
  await page.locator("#txtbx2").fill("100");

  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await page.locator("#txtbx1").click();
  await page.locator("#txtbx1").fill("100");

  await page.getByLabel("Male").check();
  await page.getByLabel("Lightly Active").check();
  await page.getByLabel("No").check();
  
  await page.locator("#foods-autocomplete").click();
  await page.getByRole("option", { name: "Carrots, mature, raw" }).click();
  await page
    .getByRole("option", { name: "Restaurant, Latino, pupusas" })
    .click();
  await page.getByLabel("Submit Button").click();
});



test("Checking if the the value of Age is iputted incorrectly ", async ({
  page,
}) => {

  // Filling out weight 
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
  await page.locator("#txtbx3").click();
  await page.locator("#txtbx3").fill("100");
  // Filling out height
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await page.locator("#txtbx2").click();
  await page.locator("#txtbx2").fill("100");
  //Filling out Age
  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await page.locator("#txtbx1").click();
  await page.locator("#txtbx1").fill("yeet");

  page.on("dialog", async (dialog) => {
    // Assert the message in the alert dialog
    expect(dialog.message()).toBe("Value Entered is not a valid number");

    // You can dismiss the dialog if necessary
    await dialog.dismiss();
  });

  await page.getByLabel("Male").check();
  await page.getByLabel("Lightly Active").check();
  await page.getByLabel("No").check();
  
  await page.locator("#foods-autocomplete").click();
  await page.getByRole("option", { name: "Carrots, mature, raw" }).click();
  await page
    .getByRole("option", { name: "Restaurant, Latino, pupusas" })
    .click();
  await page.getByLabel("Submit Button").click();
});


test("when a checkbox is no selected and submitted the form", async ({
  page,
}) => {

  // Filling out weight 
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
  await page.locator("#txtbx3").click();
  await page.locator("#txtbx3").fill("100");
  // Filling out height
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await page.locator("#txtbx2").click();
  await page.locator("#txtbx2").fill("100");
  //Filling out Age
  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await page.locator("#txtbx1").click();
  await page.locator("#txtbx1").fill("yeet");

  page.on("dialog", async (dialog) => {
    // Assert the message in the alert dialog
    expect(dialog.message()).toBe("Value Entered is not a valid number");

    // You can dismiss the dialog if necessary
    await dialog.dismiss();
  });

  await page.getByLabel("Male").check();
  await page.getByLabel("No").check();
  const initialTextContent = await page.locator('.scroll-box').textContent();
  await page.locator("#foods-autocomplete").click();
  await page.getByRole("option", { name: "Carrots, mature, raw" }).click();
  await page
    .getByRole("option", { name: "Restaurant, Latino, pupusas" })
    .click();
  await page.getByLabel("Submit Button").click();
  const finalTextContent = await page.locator(".scroll-box").textContent();
  expect(initialTextContent).toBe(finalTextContent);
});


test("Testing output if no food is selected", async ({
  page,
}) => {
  // Route to mocked error message
  await page.route("http://localhost:3233/pureplate*", (route) => {
    // Check if the request includes specific query params
    const query = route.request().url().split("?")[1];
    if (query.includes("weight=100") && query.includes("height=100")) {
      // Provide a mock response
      route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(mockedErrorMessage),
      });
    } else {
      // Optionally, let other requests pass through
      route.continue();
    }
  });
  // Filling out weight
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
  await page.locator("#txtbx3").click();
  await page.locator("#txtbx3").fill("100");

  // Filling out height
  await expect(page.getByRole("heading", { name: "Height (cm)" })).toBeVisible;
  await page.locator("#txtbx2").click();
  await page.locator("#txtbx2").fill("100");

  // Filling out age
  await expect(page.getByRole("heading", { name: "Age" })).toBeVisible;
  await page.locator("#txtbx1").click();
  await page.locator("#txtbx1").fill("100");

  await page.getByLabel("Male").check();
  await page.getByLabel("Sedentary").check();
  await page.getByLabel("No").check();

  
  await page.getByLabel("Submit Button").click();
  await page.getByText('empty request parameter').click()
});