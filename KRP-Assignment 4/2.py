TBox = [
    [('A', 'B'), 0],
    [('A', 'exists r.C'),0],
    [('A', 'exists r.L'),0],
    [('exists r.B', 'F'),0],
    [('exists r.B', 'K'),0],
    [('B', 'F', 'E'), 0],
    [('C', 'exists r.A'),0],
    [('C', 'B'),0],
    [('E', 'C'),0],
    [('E', 'D'),0],
    [('H', 'exists r.I'),0],
    [('I', 'A'),0],
    [('I', 'B'),0],
    [('J', 'D', 'H'), 0],
    [('exists r.K', 'J'),0],
    [('L', 'exists r.A'),0],
]

def match_concepts_forward(concept_1, concept_2):
    first_concept_str = concept_1[0][1]
    if 'exists' in first_concept_str:
        first_letter = first_concept_str.split('.')[1][0]
        second_letter = concept_2[0][0]
        if first_letter == second_letter:
            return True
    return False

def match_concepts_back(concept_1, concept_2):
    first_concept_str = concept_2[0][0]
    second_letter = concept_1[0][1]
    if 'exists' in first_concept_str:
        first_letter = first_concept_str.split('.')[1][0]
        if first_letter == second_letter:
            return True
    return False

Tbox_concept = ['A','B','C','D','E','F','G','H','I','J','K','L']

def apply_rule_1(concept):
    if 'exists' not in concept[0]:
        return [(concept, concept), 1]
    return None

def apply_rule_2(concept):
    if 'exists' not in concept[0]:
        return [(concept, 'top'), 1]
    return None

def apply_rule_3(concept_1, concept_2):
    if 'exists' not in concept_1[0] and concept_2[0]:
        if len(concept_1[0]) == 2 and len(concept_2[0]) == 2:
            if concept_1[0][1] == concept_1[0][0]:
                return [(concept_1[0][0], concept_2[0][1]),1]
    return None

def apply_rule_4(concept_1, concept_2, concept_3):
    if 'exists' not in concept_1[0] and concept_2[0] and concept_3[0]:
        if len(concept_1[0]) == 2 and len(concept_2[0]) == 2 and len(concept_3[0]) == 3:
            if (concept_1[0][0] == concept_2[0][0]) and (concept_1[0][1] == concept_3[0][0]) and (concept_2[0][1] == concept_3[0][1]):
                return [(concept_1[0][0], concept_3[0][2]), 1]
    return None

def apply_rule_5(concept_1, concept_2, concept_3):
    if ('exists' in concept_1[0] and concept_3[0]) and ('exists' not in concept_2[0]):
        if len(concept_1[0]) == 2 and len(concept_2[0]) == 2 and len(concept_3[0]) == 2:
            if (match_concepts_forward(concept_1, concept_2) == True) and (match_concepts_back( concept_2, concept_3) == True):
                return [(concept_1[0][0], concept_3[0][1]), 1]
    return None
def dfs(concepts, used_rules, TBox, Tbox_concept):
    if all(rule[1] != 0 for rule in TBox):
        for concept in concepts:
            if concept[0] in Tbox_concept and concept[1] in Tbox_concept:
                return True
        return False

    for rule in TBox:
        if rule[1] == 0:
            rule[1] = 1  
            new_concepts = concepts[:]  

            for concept_1 in concepts:
                new_concept = apply_rule_1(concept_1)
                if new_concept:
                    new_concepts.append(new_concept)
                new_concept = apply_rule_2(concept_1)
                if new_concept:
                    new_concepts.append(new_concept)
                for concept_2 in concepts:
                    new_concept = apply_rule_3(concept_1, concept_2)
                    if new_concept:
                        new_concepts.append(new_concept)
                    for concept_3 in concepts:
                        new_concept = apply_rule_4(concept_1, concept_2, concept_3)
                        if new_concept:
                            new_concepts.append(new_concept)
                        new_concept = apply_rule_5(concept_1, concept_2, concept_3)
                        if new_concept:
                            new_concepts.append(new_concept)
            if dfs(new_concepts, used_rules + [rule], TBox, Tbox_concept):
                return True
            rule[1] = 0  
    return False

def check_satisfiability(TBox, Tbox_concept):
    for init_inclusion in TBox:
        print(init_inclusion)
        if dfs([init_inclusion], [], TBox, Tbox_concept):
            return True
    return False

print(check_satisfiability(TBox, Tbox_concept))
        