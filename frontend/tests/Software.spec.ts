import { test, expect } from "@playwright/test";

test.beforeEach(() => {
  //await page.goto("http://localhost:8000/");
});

test("basic cases where we can see a table given basic inputs", async ({ page }) => {
  // vary parameters: weight, height, age, gender, activity levels (pick 2), growable, pick foods

  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('100');
  await page.locator('#txtbx2').fill('100');
  await page.locator('#txtbx1').fill('100');
  await page.getByLabel('Male').check();
  await page.getByLabel('Moderately Active').check();
  await page.locator('.growable-container > #rb2').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Carrots, mature, raw' }).click();
  await page.getByRole('option', { name: 'Nuts, macadamia nuts, raw' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.getByRole('paragraph').locator('div').filter({ hasText: 'Egg, yolk, driedFlour, soy, defattedSeeds, sunflower seed kernels, dry roasted, ' })).toBeVisible();

  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('55');
  await page.locator('#txtbx2').fill('44');
  await page.locator('#txtbx1').fill('33');
  await page.locator('div').filter({ hasText: /^GenderMaleFemale$/ }).getByRole('radio').nth(1).check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb1').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Restaurant, Chinese, fried rice, without meat' }).click();
  await page.getByRole('option', { name: 'Beans, Dry, Carioca (0 percent moisture)' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.getByRole('paragraph').locator('div').filter({ hasText: 'Seeds, pumpkin seeds (pepitas), rawFlour, soy, defattedCheese, parmesan, gratedC' })).toBeVisible();

  // await expect(page.getByRole("cell", { name: "Carrots, mature, raw" })).toBeVisible;
  // nutrients already fulfilled
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('10');
  await page.locator('#txtbx1').fill('10');
  await page.getByLabel('Female').check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb1').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Restaurant, Chinese, fried rice, without meat' }).click();
  await page.getByRole('option', { name: 'Beans, Dry, Carioca (0 percent moisture)' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.locator('.scroll-box')).toBeVisible();
});

test("clicking submit without filling out one or more fields", async ({ page }) => {
  // w/o filling out one field
  // missing weight on frontend side
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx2').fill('50');
  await page.locator('#txtbx1').fill('50');
  await page.getByLabel('Male').check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb2').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Carrots, mature, raw' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.locator('.scroll-box')).toBeVisible();

  // missing foods which are passed to backend side
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('10');
  await page.locator('#txtbx1').fill('10');
  await page.locator('div').filter({ hasText: /^GenderMaleFemale$/ }).getByRole('radio').nth(1).check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb2').check();
  await page.getByLabel('Submit Button').click();
  await expect(page.getByText('empty request parameter')).toBeVisible();

  // w/o filling out all fields
  await page.goto("http://localhost:8000/software");
  await page.getByLabel('Submit Button').click();
  await expect(page.locator('.scroll-box')).toBeVisible();
});

test("non-numeric age, weight, and height inputs", async ({ page }) => {

await page.goto("http://localhost:8000/software");
await page.locator('#txtbx3').fill('10');
await page.locator('#txtbx2').fill('10');
await page.locator('#txtbx1').fill('hello tim');
page.on("dialog", async (dialog) => {
  // Assert the message in the alert dialog
  expect(dialog.message()).toBe("Value entered is not a valid number");
  // You can dismiss the dialog if necessary
  await dialog.dismiss();
});
// non numeric value for weight
await page.goto("http://localhost:8000/software");
await page.locator('#txtbx3').fill('10');
await page.locator('#txtbx2').fill('hello molly');

page.on("dialog", async (dialog) => {
  // Assert the message in the alert dialog
  expect(dialog.message()).toBe("Value entered is not a valid number");
  // You can dismiss the dialog if necessary
  //await dialog.dismiss();
});
// non numeric value for height
await page.goto("http://localhost:8000/software");
await page.locator('#txtbx3').fill('10');
await page.locator('#txtbx2').fill('hello molly');

page.on("dialog", async (dialog) => {
  // Assert the message in the alert dialog
  expect(dialog.message()).toBe("Value entered is not a valid age");
  // You can dismiss the dialog if necessary
  await dialog.dismiss();
});
});

test("negative/0 for age, weight, and height inputs", async ({ page }) => {
 //negative age -> expect a error popup 
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('10');
  await page.locator('#txtbx1').fill('-5');
  page.on("dialog", async (dialog) => {
    // Assert the message in the alert dialog
    expect(dialog.message()).toBe("Value entered is not a valid number");
    // You can dismiss the dialog if necessary
    await dialog.dismiss();
  });
  //negative height value
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('-5');
  page.on("dialog", async (dialog) => {
  // Assert the message in the alert dialog
  expect(dialog.message()).toBe("Value entered is not a valid number");
  // You can dismiss the dialog if necessary
  await dialog.dismiss();
  });
  //negative weight 
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('-5');
  page.on("dialog", async (dialog) => {
  // Assert the message in the alert dialog
  expect(dialog.message()).toBe("Value entered is not a valid number");
  // You can dismiss the dialog if necessary
  await dialog.dismiss();
  });
  //reccomendations could not be received for 0 values 
  //0 value for age
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('10');
  await page.locator('#txtbx1').fill('0');
  await page.getByLabel('Female').check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb1').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Restaurant, Chinese, fried rice, without meat' }).click();
  await page.getByRole('option', { name: 'Beans, Dry, Carioca (0 percent moisture)' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.locator(".scroll-box")).toContainText("recommendations could not be retrieved")
  //0 value for height
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('10');
  await page.locator('#txtbx2').fill('0');
  await page.locator('#txtbx1').fill('20');
  await page.getByLabel('Female').check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb1').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Restaurant, Chinese, fried rice, without meat' }).click();
  await page.getByRole('option', { name: 'Beans, Dry, Carioca (0 percent moisture)' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.locator(".scroll-box")).toContainText("recommendations could not be retrieved")
  //0 value for wight 
  await page.goto("http://localhost:8000/software");
  await page.locator('#txtbx3').fill('0');
  await page.locator('#txtbx2').fill('30');
  await page.locator('#txtbx1').fill('20');
  await page.getByLabel('Female').check();
  await page.getByLabel('Very Active').check();
  await page.locator('.growable-container > #rb1').check();
  await page.locator('#foods-autocomplete').click();
  await page.getByRole('option', { name: 'Restaurant, Chinese, fried rice, without meat' }).click();
  await page.getByRole('option', { name: 'Beans, Dry, Carioca (0 percent moisture)' }).click();
  await page.getByLabel('Submit Button').click();
  await expect(page.locator(".scroll-box")).toContainText("recommendations could not be retrieved")
});