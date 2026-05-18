from PIL import Image

src = "images/App Logo Component_margin copy.png"
img = Image.open(src).convert("RGBA")
pixels = img.load()
w, h = img.size

# Pass 1: Remove background, keep only solid purple ticks
for y in range(h):
    for x in range(w):
        r, g, b, a = pixels[x, y]
        # Purple ticks: R~88, G~81, B~219 — keep only clearly purple + opaque
        is_purple = r < 130 and g < 130 and b > 160 and a > 200
        if not is_purple:
            pixels[x, y] = (0, 0, 0, 0)

# Pass 2: Remove stray edge pixels (alpha < 200 after first pass)
for y in range(h):
    for x in range(w):
        r, g, b, a = pixels[x, y]
        if a < 200:
            pixels[x, y] = (0, 0, 0, 0)

bbox = img.getbbox()
print(f"Tight bounding box: {bbox}")
cropped = img.crop(bbox)
cw, ch = cropped.size
print(f"Tick marks only: {cw}x{ch}")
cropped.save("images/ticks_only.png")

# Generate adaptive icon foregrounds (transparent bg, ticks centered)
for folder, size in [("mipmap-mdpi",108),("mipmap-hdpi",162),("mipmap-xhdpi",216),("mipmap-xxhdpi",324),("mipmap-xxxhdpi",432)]:
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    target = int(size * 0.38)  # 38% for more breathing room
    scale = min(target / cw, target / ch)
    nw, nh = int(cw * scale), int(ch * scale)
    resized = cropped.resize((nw, nh), Image.LANCZOS)
    canvas.paste(resized, ((size-nw)//2, (size-nh)//2), resized)
    canvas.save(f"app/src/main/res/{folder}/ic_launcher_foreground.png")
    print(f"  FG: {folder} ({size}px)")

# Legacy launcher icons (white bg + centered ticks)
for folder, size in [("mipmap-mdpi",48),("mipmap-hdpi",72),("mipmap-xhdpi",96),("mipmap-xxhdpi",144),("mipmap-xxxhdpi",192)]:
    canvas = Image.new("RGBA", (size, size), (255, 255, 255, 255))
    target = int(size * 0.42)
    scale = min(target / cw, target / ch)
    nw, nh = int(cw * scale), int(ch * scale)
    resized = cropped.resize((nw, nh), Image.LANCZOS)
    canvas.paste(resized, ((size-nw)//2, (size-nh)//2), resized)
    canvas.save(f"app/src/main/res/{folder}/ic_launcher.png")
    canvas.save(f"app/src/main/res/{folder}/ic_launcher_round.png")
    print(f"  IC: {folder} ({size}px)")

print("\nDONE!")
