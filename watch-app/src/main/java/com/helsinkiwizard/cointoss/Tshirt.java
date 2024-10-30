package com.helsinkiwizard.cointoss;

import java.util.List;

class Child {
    public void enjoyDay() {

    }
}

class Dad {
    public void childActions() {
        List<Child> children = List.of(
                new Ben(),
                new David(),
                new Lincoln(),
                new Jenny()
        );

        for (int i = 0; i < children.size(); i++) {
            Child child = children.get(i);
            try {
                child.enjoyDay();
            } catch (Exception e) {
                System.out.println("Dang weiner kid");
            }
        }
    }
}

class Ben extends Child {
    @Override
    public void enjoyDay() {
        playMario();
        snuggleDad();
    }
}

class David extends Child {
    @Override
    public void enjoyDay() {

    }
}

class Lincoln extends Child {
    @Override
    public void enjoyDay() {

    }
}

class Jenny extends Child {
    @Override
    public void enjoyDay() {

    }
}
