import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModeracionCategoriasComponent } from './moderacion-categorias.component';

describe('ModeracionCategoriasComponent', () => {
  let component: ModeracionCategoriasComponent;
  let fixture: ComponentFixture<ModeracionCategoriasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModeracionCategoriasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModeracionCategoriasComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
