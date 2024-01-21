import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000");
});

test("I see basic elements on the home page", async ({ page }) => {
  await expect(page.getByRole("button", { name: "Home" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Our Mission" })).toBeVisible;
  await expect(page.locator('[id="pottedPlant\\ "]')).toBeVisible;
});

test("I can route between pages", async ({ page }) => {
  await expect(page.getByRole("button", { name: "Home" })).toBeVisible;
  await expect(page.getByRole("heading", { name: "Our Mission" })).toBeVisible;
  await expect(page.locator('[id="pottedPlant\\ "]')).toBeVisible;

  // route to meet the team
  await page.getByRole("button", { name: "Meet The Team" }).click();
  await expect(page.getByRole('heading', { name: 'Wilson Vo Frontend and UI/UX' })).toBeVisible;

  // route to software page
  await page.getByRole("button", { name: "Software" }).click();
  await expect(page.getByRole("heading", { name: "Weight (kg)" })).toBeVisible;
});